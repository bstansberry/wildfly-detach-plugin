# wildfly-detach-plugin

Maven plugin that detaches artifacts from a project during the build.

## Usage

Here's an example use in a build that earlier used the maven-assembly-plugin
to produce tar.gz files that are wanted for use elsewhere. But we don't want 
to install them in the local maven repo or deploy them to a remote repo.

NOTE: The maven-assembly-plugin has an `attach` config operation that would let
us not attach the tars. But we want the maven-gpg-plugin to sign and produce
.asc files, so we need them to be attached until that is done.

```
<!-- GPG signing is configured elsewhere. Just redeclare the execution here
     so the wildfly-detach-plugin runs after and can detach the asc files -->
<plugin>
    <groupId>org.apache.maven.plugins</plugin>
    <artifactId>maven-gpg-plugin</plugin>
    <executions>
        <execution>
            <id>gpg-sign</id>
            <phase>verify</phase> <!-- the normal phase for the 'sign' mojo -->
        </execution>
    </executions>
</plugin>
<plugin>
    <groupId>org.wildfly.plugins</plugin>
    <artifactId>wildfly-detach-plugin</plugin>
    <executions>
        <execution>
            <!-- We built tars for local use but we don't
                 want to install or deploy them. -->
            <id>detach-tars</id>
            <phase>verify</phase>
            <configuration>
                <regex>\S{1,}tar\.gz\S*</regex>
            </configuration>
        </execution>
    </executions>
</plugin>
```
