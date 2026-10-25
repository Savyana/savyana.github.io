CS 499 Milestone Three - Algorithms and Data Structures
Savanna Neace

ENHANCEMENT
The original Treasure Hunt Game used deep Q-learning to navigate an 8x8 maze. The enhanced version adds A* pathfinding with Manhattan distance and reports path cost, moves, and states explored. The notebook also restores helper functions missing from the exported original notebook so the deep Q-learning code can be trained and compared with A*.

TESTED A* RESULT
Start: (0, 0)
Goal: (7, 7)
Moves: 24
Path cost: 24
States explored: 50

FILES
Original/ - copies of the supplied original artifacts
Enhanced/TreasureMaze_Enhanced.py - original maze class plus A* methods
Enhanced/AStarPathfinding.py - standalone A* implementation
Enhanced/GameExperience_Enhanced.py - supporting experience-replay class
Enhanced/TreasureHuntGame_Algorithms_Enhanced.ipynb - enhanced notebook
Enhanced/requirements.txt - Python dependencies

The deep Q-learning comparison requires TensorFlow and model training. The A* portion was tested independently against the original maze.
