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
  * The repository URL is: https://github.com/COBWEB-DEVELOPERS-2024/COBWEB-main-current-version and the branch is "master"
* Wait for Eclipse to fetch the dependencies using Maven and build the project
* Modify and run the code!

### Setting up the development environment for Intellij IDEA Ultimate Edition
* Install Intellij IDEA Ultimate Edition: https://www.jetbrains.com/idea/download/?section=windows
* Open the IDE
* Go to File -> New -> Project From Version Control
* Select "Git" as the version control
* Paste the following URL for the Git repository: https://github.com/COBWEB-DEVELOPERS-2024/COBWEB-main-current-version.git
* Wait for Intellij IDEA to fetch the dependencies and build the project
* Modify and run the code!

### Note for developers
* COBWEB will only compile if you go to the "CobwebApplicationRunner.java" file in your IDE, which is located in src/main/java/org/cobweb/cobweb2/ui/swing

### Description
COBWEB is a piece of software that was developed to explore how components of systems (people, animals, bacteria, etc) adapt to environmental change and environmental variability. The software contains two major components: (1) a population of agents that move, eat and reproduce and (2) the resources that the agents consume, which are also variable in location. The agents make decisions on when to move, where to move and when to eat. In combination with optional communication between agents and memory, the population is able to adapt to some environments and fail to adapt in others. This process can result in the occurrence of surprising events, whether it is a sudden extinction after a long period of stability, a recovery from what appears to be certain extinction or a sudden change in population for no apparent reason. These surprising events can be expected in any complex system, and their occurrence in this simple abstraction of complexity is a very important aspect of COBWEB software.

The COBWEB agents are powered by an artificial intelligence tool called a genetic algorithm, which is a behavioural strategy. These strategies are randomly created when we initialize the model. The resources, or the environmental variability, are simulated with another common AI too, a cellular automaton. 

COBWEB is very interactive, allowing the user to set many parameters that either affect the agents or the resource production. Parameters such as energy expenditure, resource growth rate, energy requirements for different actions and the energy derived from different resources are all accessed through a user interface that is very easy to use. Once the parameters have been set, the environment is displayed graphically, as a two-dimensional grid populated with agents represented by triangles and resources represented by coloured squares. Barriers to movement are represented by black squares. The agents can choose to move, turn, eat or communicate to increase their chances of survival and reproduction.

The population evolves in a Darwinian sense, as those agents that are best adapted to the environment tend to reproduce most often, and through mutation during reproduction (the rate of which can be controlled by the user), the system can experiment with new strategies. The agents that are not well adapted to survival are eliminated during the simulation. The user has the option to change the environment in small or large increments at any time during the simulation in order to test the adaptability of the population to change.
