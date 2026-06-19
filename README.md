# wildfly-detach-plugin
Maven plugin that detaches artifacts from a project during the build

## Usage

```
<plugin>
	<groupId>org.wildfly.plugins</plugin>
	<artifactId>wildfly-detach-plugin</plugin>
	<executions>
		<execution>
			<id>detach-tars</id>
			<phase>verify</phase>
			<configuration>
				<regex>\S{1,}tar\.gz\S*</regex>
			</configuration>
		</execution>
	</executions>
</plugin>
```
