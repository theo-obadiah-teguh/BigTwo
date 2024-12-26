# BigTwo
A traditional card game with multiplayer functionality support for up to four people, implemented with a Java client-server model and a Swing GUI. This was a semester-long project for the course COMP2396: Object-Oriented Programming and Java by the University of Hong Kong.

## Recorded Demo
https://github.com/user-attachments/assets/1da4c0d2-137f-4bed-a6d3-479d348fc4d0

## Some Thoughts
This was my first time coding a fully functional and responsive GUI, with a proper client-server model. Additionally, this course also taught me how to write and appreciate proper documentation for large-scale projects. Documentation for the classes in this project are located in the `javadoc` directory. I also learned how to use basic network sockets and the fundamentals of serialization and threading. There are some additional edge cases to consider in the future, such as handling client reconnection. Furthermore, the server should ideally be hosted on an AWS EC2 or VM to make the game truly playable across devices.

## Compilation and Execution Instructions:
1. Make sure you have Git installed on your device.
1. Open your terminal, choose your desired directory and clone the repository by executing `git clone https://github.com/theo-obadiah-teguh/BigTwo.git`.
1. Enter your GitHub username and token (if applicable).
1. Enter the newly cloned directory with `cd BigTwo`.
1. Execute `javac *.java` to compile all of the dependencies.
1. Execute `java BigTwoServer` to activate the game server.
1. Execute `java BigTwo` four times to add four players to the game.
1. Alternatively, run `java BigTwo` before `java BigTwoServer` and enter the game with the connect button in the top left menu.
1. The game starts when all clients have joined. Afterwards, have fun playing the game!
