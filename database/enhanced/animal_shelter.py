"""MongoDB CRUD helper for the Grazioso Salvare dashboard.

CS 499 database enhancement:
- validates inputs before database operations
- supports safer configuration through environment variables
- adds optional projection, sorting, and result limits for reads
- uses specific PyMongo exceptions instead of catching every exception
- adds connection checking and a clean close method
"""

import os
from typing import Any, Dict, Iterable, List, Optional, Sequence, Tuple

from pymongo import MongoClient
from pymongo.errors import PyMongoError


class AnimalShelter:
    """Provide CRUD operations for the AAC animal shelter collection."""

    def __init__(
        self,
        username: Optional[str] = None,
        password: Optional[str] = None,
        host: Optional[str] = None,
        port: Optional[int] = None,
        database_name: Optional[str] = None,
        collection_name: Optional[str] = None,
    ) -> None:
        self.database_name = database_name or os.getenv("AAC_DB_NAME", "AAC")
        self.collection_name = collection_name or os.getenv("AAC_COLLECTION", "animals")
        host = host or os.getenv("AAC_DB_HOST", "localhost")
        port = int(port or os.getenv("AAC_DB_PORT", "27017"))

        username = username or os.getenv("AAC_DB_USERNAME")
        password = password or os.getenv("AAC_DB_PASSWORD")

        connection_options = {
            "host": host,
            "port": port,
            "serverSelectionTimeoutMS": 5000,
        }

        if username and password:
            connection_options.update(
                {
                    "username": username,
                    "password": password,
                    "authSource": self.database_name,
                }
            )

        self.client = MongoClient(**connection_options)
        self.database = self.client[self.database_name]
        self.collection = self.database[self.collection_name]

    @staticmethod
    def _require_dict(value: Any, name: str, allow_empty: bool = True) -> Dict[str, Any]:
        """Validate that a CRUD argument is a dictionary."""
        if not isinstance(value, dict):
            raise ValueError(f"{name} must be a dictionary.")
        if not allow_empty and not value:
            raise ValueError(f"{name} cannot be empty.")
        return value

    def ping(self) -> bool:
        """Return True when MongoDB can be reached."""
        try:
            self.client.admin.command("ping")
            return True
        except PyMongoError:
            return False

    def create(self, data: Dict[str, Any]) -> bool:
        """Insert one animal record and return whether the insert succeeded."""
        data = self._require_dict(data, "data", allow_empty=False)
        try:
            result = self.collection.insert_one(data.copy())
            return result.inserted_id is not None
        except PyMongoError as exc:
            print(f"Database error while inserting document: {exc}")
            return False

    def read(
        self,
        query: Optional[Dict[str, Any]] = None,
        projection: Optional[Dict[str, int]] = None,
        limit: int = 0,
        sort: Optional[Sequence[Tuple[str, int]]] = None,
    ) -> List[Dict[str, Any]]:
        """Return records matching a query with optional projection, sort, and limit."""
        query = {} if query is None else self._require_dict(query, "query")

        if projection is not None:
            projection = self._require_dict(projection, "projection")

        if not isinstance(limit, int) or limit < 0:
            raise ValueError("limit must be a non-negative integer.")

        try:
            cursor = self.collection.find(query, projection)
            if sort:
                cursor = cursor.sort(list(sort))
            if limit:
                cursor = cursor.limit(limit)
            return list(cursor)
        except PyMongoError as exc:
            print(f"Database error while reading documents: {exc}")
            return []

    def update(self, query: Dict[str, Any], new_data: Dict[str, Any]) -> int:
        """Update matching records and return the number of modified documents."""
        query = self._require_dict(query, "query", allow_empty=False)
        new_data = self._require_dict(new_data, "new_data", allow_empty=False)

        try:
            result = self.collection.update_many(query, {"$set": new_data})
            return result.modified_count
        except PyMongoError as exc:
            print(f"Database error while updating documents: {exc}")
            return 0

    def delete(self, query: Dict[str, Any]) -> int:
        """Delete matching records and return the number of deleted documents."""
        query = self._require_dict(query, "query", allow_empty=False)

        try:
            result = self.collection.delete_many(query)
            return result.deleted_count
        except PyMongoError as exc:
            print(f"Database error while deleting documents: {exc}")
            return 0

    def count(self, query: Optional[Dict[str, Any]] = None) -> int:
        """Return the number of records matching a query."""
        query = {} if query is None else self._require_dict(query, "query")
        try:
            return self.collection.count_documents(query)
        except PyMongoError as exc:
            print(f"Database error while counting documents: {exc}")
            return 0

    def close(self) -> None:
        """Close the MongoDB client connection."""
        self.client.close()
