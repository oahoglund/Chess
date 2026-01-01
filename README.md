# Chess

A simple chess game implemented in Java using a graphical user interface. This was made when I was learning java in order improve my abilities.

## Features:

### Graphical Display
Uses JPanel to make a game window that is interactable with the user. The actual pieces being shown are custom made 8-bit.
![Game start](screenshots/initial_position.png)

### Display possible moves
When selecting a piece it will only be shown what legal moves it can do as indicated by the green circles. Clicking on other parts of the board will deselect the piece. Under you can see the moves that the white queen can do.

![Smart move](screenshots/posible_moves.png)

### Detect illegal moves
The program will detect any move that would put the king in check and not show them. This can be seen under where the king cant move left because of the queen blocking. It will also find any eventual discovered checks.

![Illegal move](screenshots/illegal_moves.png)

### Advanced chess moves

#### Double pawn move
I will not show a scree shot specifically for this as one can see it in a lot of the other screenshots.

#### En passant
To punish any pesky pawns trying to run away one can do an en passant as shown under:

![En passant](screenshots/en_passant.png)

#### Promotion
Currently all pawns that make it the the last row will promote to queens only, this may be changed in a future version.

#### Castling
Both long and short castling is aviable. It is checked if it is legal first, so if for example the oposing queen is blocking the castling it cant be done. It will also check if the king or rook have already moved. An example of a succesfull castling is shown under:

![Castle1](screenshots/castling_1.png)
![Castle2](screenshots/castling_2.png)

### Check Mate detection
The program will always check for checkmates. When it does it will show a check mate graphic. (In order to play again one has to close and open the program again)

![Checkmate](screenshots/chechmate.png)

## Requirements
- Java JDK 17 (or the version you are using)
- No global Gradle installation required (Gradle Wrapper included)

## Project Structure
- `src/` – Java source code
- `res/` – Game resources (piece images, etc.)
- `build.gradle` – Gradle build configuration

## Build
To compile the project, run:

```bash
./gradlew build
```
(On Windows: gradlew build)

## Run
To run the application:

```bash
./gradlew run
```
(On Windows: gradlew run)

## Notes
- This project uses the Gradle Wrapper, so Gradle does not need to be installed separately.
- Build output is generated in the build/ directory.
- The project follows a simple custom source layout (src/ and res/).

## Author
Oscar Albert Höglund
