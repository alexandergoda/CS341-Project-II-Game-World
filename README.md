# Game World

CS 341 Software Engineering, Project II · University of Redlands

A Java 11 Swing application with four animated objects. The red square identifies the object controlled by the arrow keys. Press Tab to move the square to the next object. Release the arrow to stop. If you hold two arrows the selected object can use, the newer one takes priority. Releasing it returns control to the arrow still held.

## Run in Eclipse

1. Open **File → Import → General → Existing Projects into Workspace**.
2. Choose **Select archive file**, browse to **CS341-Game-World-Eclipse.zip**, select **CS341-Project-II-Game-World**, and click **Finish**. For an already unzipped copy, use **Select root directory** and select the project folder.
3. In Package Explorer, open **src/main/java → gameworld → GameWorldApp.java**. Choose **Run As → Java Application**.
4. To run the tests, right-click **src/test/java** and choose **Run As → JUnit Test**. The Eclipse project uses the JavaSE-11 execution environment and the JUnit 5 container supplied with Eclipse Java Development Tools.

## Run the packaged app

With Java 11 or newer installed, unzip the download, open a terminal in the project folder and run:

```sh
java -jar dist/game-world.jar
```

To build from source with JDK 11 or newer and Maven 3.9, run:

```sh
mvn clean package
java -jar target/game-world.jar
```

The Maven build runs the JUnit suite and embeds the sprites in the runnable JAR. Its first build downloads the Maven plugins and JUnit.

## Try the controls

| Tab order | With the red frame | Without the red frame |
| --- | --- | --- |
| D · Blue | All four arrows | Waits in place |
| A · Yellow | Up and Down | Patrols a vertical lane on the left, reversing at the top and bottom |
| B · Red | All four arrows | Patrols all four outer edges and turns at each corner |
| C · Purple (pink) | Left and Right | Patrols an upper horizontal lane, reversing at each side |

Blue starts selected. Tab wraps from purple back to blue. When selection changes, the former selection resumes its own behavior from its current position. If you steer red into the interior, it moves back to the nearest wall after you switch away. Along the perimeter it travels right across the bottom, up the right edge, left across the top, and down the left edge. A new arrow press directs the newly selected object.

## Project guide

| Location | Contents |
| --- | --- |
| `src/main/java/gameworld/model` | Object hierarchy, movement rules, selection order and boundaries |
| `src/main/java/gameworld/view` | Sprite loading, canvas rendering and selection frame |
| `src/main/java/gameworld/controller` | Swing timer and keyboard input |
| `src/main/resources/sprites` | The 12 original course PNGs from the student starter archive |
| `src/test/java/gameworld` | Model, keyboard, rendering and sprite-resource tests |
| `docs/Game-World-UML.pdf` | Class diagrams for the MVC structure and movement hierarchy |
| `docs/Game-World-Use-Cases.pdf` | Six player scenarios and their observable outcomes |
| `docs/Game-World-Design-Pattern.pdf` | Template Method design justification |

The horizontal course filenames for B, C and D are reversed relative to the visible artwork. SpriteAtlas maps each logical direction to the original PNG that points that way.
