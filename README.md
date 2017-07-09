# TicTacToe
Famous game on Java sockets.

To start a game, you must clone this repository, run server and connect clients.

1. Clone this repository.
```
git clone https://github.com/ikasyk/TicTacToe.git
```

2. Start server in TicTacToeServer folder. Default port is **1235**. You can change it in Main.java.
3. Start client in TicTacToe folder. If you change the default port, you must change it in Main.java.
4. You can write commands in your client console.

If you use IDE (for example, JetBrains IntelliJ IDEA), you should open two different projects for client and server.

### Commands

* `/info`, `/hello` - shows information about user (ID and login).
* `/login [login]` - changes your name to [login].
* `/invite [user_id]` - sends invitation to player with ID [user_id].
* `/accept [user_id]` - accepts invitation from player with ID [user_id].
* `/myinvites` - list of all users that sent invitation to you.
* `/list` - list of all users connected to server.
* `/move [X] [Y]` - *only in game mode* - moves to cell with coordinates ([X]; [Y]).
