# COBWEB 2
COBWEB (Complexity & Organized Behaviour Within Environmental Bounds) is a computer simulation model which can be used for research in a variety of fields such as ecology, health, psychology, economic geography, climate change impacts and ethics.


### Running COBWEB
* Java is required, you can get it here: https://java.com/download/
* Get the latest COBWEB jar file: https://github.com/COBWEB-ca/cobweb2/releases

### Setting up the development environment for Eclipse
* Install Eclipse IDE for Java Developers: http://eclipse.org/downloads/
* Install EGit: http://www.vogella.com/tutorials/EclipseGit/article.html#eclipseinstallation
* Configure EGit: http://www.vogella.com/tutorials/EclipseGit/article.html#egitconfiguration (just steps 5.1 and 5.2 should be enough)
* Clone COBWEB from GitHub: http://www.vogella.com/tutorials/EclipseGit/article.html#clone_repositoy
  * The repository URL is: https://github.com/COBWEB-DEVELOPERS-2024/COBWEB-main-current-version and the branch is "dev-javafx"
* Wait for Eclipse to fetch the dependencies using Maven and build the project
* Modify and run the code!

### Setting up the development environment for Intellij IDEA Ultimate Edition
* Install Intellij IDEA Ultimate Edition: https://www.jetbrains.com/idea/download/?section=windows
* Open the IDE
* Go to File -> New -> Project From Version Control
* Select "Git" as the version control
* Paste the following URL for the Git repository: https://github.com/COBWEB-DEVELOPERS-2024/COBWEB-main-current-version/tree/dev-javafx.git
* Wait for Intellij IDEA to fetch the dependencies and build the project
* Modify and run the code!

### Note for developers
* COBWEB will only compile if you go to the "CobwebApplicationRunner.java" file in your IDE, which is located in src/main/java/org/cobweb/cobweb2/ui/swing

### Note: This is WIP/experimental build of COBWEB using JFX-22 and JDK-22. 
### It will NOT compile; some of the custom-made libraries for Swing need to be updated to work for JavaFX.

### Install JavaFX (IntelliJ IDEA Ultimate Edition)
* Download openJDK-22 in IntelliJ
* Download JavaFX 22: https://gluonhq.com/products/javafx/
* Extract the library to C:\Program Files\Java
* Open the project in Intellij IDEA
* Go to File -> Project Structure -> Libraries
* Press the '+' button, and add the JavaFX library
  * The library folder should be called 'lib,' which is located under C:\Program Files\Java\javafx-sdk-22.0.2
* Navigate over to the 'Modules,' which is under the Project Settings section on the left
* Once opened, go to 'Dependencies,' and ensure that 'lib' was added. If it's not present, add it in manually like before.
* The libraries should be loaded. 
  * If not, try right-clicking the project and reloading the files from disk, or restart the IDE.

### ** TODO **
* Migrate everything in org.cobweb.cobweb2.ui.swing, replacing Swing and/or AWT libraries method calls with the ones defined in JavaFX's libraries.
* Change CobwebApplicationRunner.java to be the JavaFX equivalent of a main() method/entry point for compiling.
* Refactor org.cobweb.cobweb2.ui.swing to org.cobweb.cobweb2.ui.javafx after migrating everything class/method in the package.
* Change the compilers' settings for IntelliJ to use openjdk-22 by default when compiling the program, and ensure that it compiles correctly after the migration is complete.
  * Hasn't been configured yet since the current state of the program cannot be compiled at this point.