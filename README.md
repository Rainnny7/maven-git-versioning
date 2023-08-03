# maven-git-versioning

This plugin seamlessly converts standard "-SNAPSHOT" versions into a more descriptive format, "
-dev-{gitDepth}-{gitHash}", allowing you to gain valuable insights into the exact commit from which each jar was built.

# Usage
1. Add the plugin to your pom.xml:
    ```xml
    <build>
        <plugins>
            <plugin>
                <groupId>me.braydon</groupId>
                <artifactId>git-versioning-maven-plugin</artifactId>
                <version>1.0</version>
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <goals>
                            <goal>versioning</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    
    <pluginRepositories>
        <pluginRepository>
            <id>rainnny-repo-public</id>
            <url>https://maven.rainnny.club/public</url>
        </pluginRepository>
    </pluginRepositories>
    ```
2. Build your project with `mvn package` (or `mvn git-versioning:versioning` if you just want to see data)