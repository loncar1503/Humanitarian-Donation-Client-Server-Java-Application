# Humanitarian Donation Client-Server Application

This is a console-based client-server Java application for making humanitarian donations. The project was developed as part of a networking programming assignment.

## Features
- User registration and login
- Secure donation processing with predefined card validation
- Viewing total collected funds
- Viewing transaction history
- Multi-client support

## Technical Details
- The server handles multiple clients simultaneously
- Data persistence is ensured even after server restarts
- Error handling for communication and transactions
- Secure validation of card numbers and CVV codes

## How It Works
1. The client connects to the server.
2. Users can register or log in.
3. A logged-in user can donate by entering a predefined card’s CVV and amount.
4. Users can view total donations and their transaction history.

