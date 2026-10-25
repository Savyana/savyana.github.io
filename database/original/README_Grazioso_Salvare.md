# Grazioso Salvare Dashboard README

## Required Functionality
This dashboard connects to the AAC MongoDB database and displays animal shelter outcome records for Grazioso Salvare. It includes the Grazioso Salvare logo, a creator identifier, rescue-type filters, an interactive data table, a breed pie chart, and a geolocation map.

## Tools Used
- MongoDB: stores the Austin Animal Center animal outcomes data.
- Python: runs the dashboard and database connection logic.
- PyMongo: connects Python to MongoDB.
- Dash/JupyterDash: creates the web dashboard interface.
- Dash DataTable: displays sortable and filterable animal records.
- Plotly Express: creates the breed pie chart.
- Dash Leaflet: creates the geolocation map.

## Steps to Reproduce
1. Make sure MongoDB is running.
2. Import the Austin Animal Center CSV into the AAC database and animals collection.
3. Place `ProjectTwoDashboard_completed.ipynb`, `animal_shelter.py`, and `Grazioso Salvare Logo.png` in the same directory.
4. Open `ProjectTwoDashboard_completed.ipynb` in Jupyter Notebook.
5. Confirm the username and password match your MongoDB user.
6. Run the notebook cell.
7. Use the radio buttons to test Water Rescue, Mountain or Wilderness Rescue, Disaster or Individual Tracking, and Reset.

## Challenges Encountered
One challenge was making sure the dashboard filters matched the rescue-dog requirements exactly. This was handled by using MongoDB queries that filter by animal type, breed, sex, and age in weeks. Another challenge was making sure the map and pie chart updated when the data table changed. This was handled using Dash callbacks tied to the filtered table data.

## Screenshots
Add screenshots below after testing the dashboard:

### Reset / Unfiltered Dashboard

### Water Rescue Filter

### Mountain or Wilderness Rescue Filter

### Disaster or Individual Tracking Filter
