"""A* search utilities for the CS 499 Treasure Hunt enhancement."""

import heapq


def manhattan_distance(cell, goal):
    return abs(cell[0] - goal[0]) + abs(cell[1] - goal[1])


def astar_search(maze, start=(0, 0), goal=None):
    """Return an optimal path and search metrics for a 2-D NumPy maze.

    A value of 0.0 is treated as blocked; nonzero cells are traversable.
    """
    if goal is None:
        goal = (maze.shape[0] - 1, maze.shape[1] - 1)

    frontier = [(manhattan_distance(start, goal), 0, start)]
    came_from = {}
    g_score = {start: 0}
    closed = set()
    explored = 0

    while frontier:
        _, cost, current = heapq.heappop(frontier)
        if current in closed:
            continue
        closed.add(current)
        explored += 1

        if current == goal:
            path = [current]
            while current in came_from:
                current = came_from[current]
                path.append(current)
            path.reverse()
            return path, {
                "path_cost": cost,
                "moves": len(path) - 1,
                "states_explored": explored,
            }

        row, col = current
        for nr, nc in ((row-1, col), (row+1, col), (row, col-1), (row, col+1)):
            if 0 <= nr < maze.shape[0] and 0 <= nc < maze.shape[1] and maze[nr, nc] != 0.0:
                neighbor = (nr, nc)
                new_cost = cost + 1
                if new_cost < g_score.get(neighbor, float("inf")):
                    g_score[neighbor] = new_cost
                    came_from[neighbor] = current
                    priority = new_cost + manhattan_distance(neighbor, goal)
                    heapq.heappush(frontier, (priority, new_cost, neighbor))

    return None, {"path_cost": None, "moves": None, "states_explored": explored}
