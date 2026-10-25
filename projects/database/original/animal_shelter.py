from pymongo import MongoClient


class AnimalShelter(object):

    def __init__(self, username=None, password=None):
        DB = "AAC"
        COL = "animals"

        if username and password:
            self.client = MongoClient(
                host="localhost", port=27017, username=username, password=password, authSource=DB
            )
        else:
            self.client = MongoClient("mongodb://localhost:27017")

        self.database = self.client[DB]
        self.collection = self.database[COL]

    def create(self, data):
        if data is not None:
            try:
                result = self.collection.insert_one(data)
                return True if result.inserted_id else False
            except Exception as e:
                print("Error inserting document:", e)
                return False
        else:
            raise Exception("Nothing to save, because data parameter is empty")

    def read(self, query):
        if query is not None:
            try:
                data = self.collection.find(query)
                return list(data)
            except Exception as e:
                print("Error reading documents:", e)
                return []
        else:
            raise Exception("Read query cannot be empty")

    def update(self, query, newData):
        if query is not None and newData is not None:
            try:
                result = self.collection.update_many(query, {"$set": newData})
                return result.modified_count
            except Exception as e:
                print("Error updating document:", e)
                return 0
        else:
            raise Exception("Update parameters cannot be empty")

    def delete(self, query):
        if query is not None:
            try:
                result = self.collection.delete_many(query)
                return result.deleted_count
            except Exception as e:
                print("Error deleting document:", e)
                return 0
        else:
            raise Exception("Delete query cannot be empty")
