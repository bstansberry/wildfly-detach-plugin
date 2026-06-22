/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */

// Find the tar.gz file in the target directory
File targetDir = new File(basedir, "target")
File[] tarGzFiles = targetDir.listFiles(new FilenameFilter() {
    boolean accept(File dir, String name) {
        return name.endsWith("-test.tar.gz") && !name.endsWith(".asc")
    }
})

if (tarGzFiles == null || tarGzFiles.length == 0) {
    println("VERIFY ERROR: No tar.gz file found in target directory")
    return false
}

File tarGzFile = tarGzFiles[0]
String tarGzFileName = tarGzFile.name
println("SUCCESS: Found tar.gz in target directory: " + tarGzFile.absolutePath)

// Extract version and artifact info from the filename
// Expected format: detach-tar-test-VERSION-test.tar.gz
String version = tarGzFileName.replace("detach-tar-test-", "").replace("-test.tar.gz", "")
println("INFO: Detected version: " + version)

// Check that the tar.gz file was NOT installed to the local repository
File localRepoBase = new File(basedir, "../../local-repo")
File localRepoArtifact = new File(localRepoBase, "org/wildfly/plugins/test/detach-tar-test/${version}/detach-tar-test-${version}-test.tar.gz")

if (localRepoArtifact.exists()) {
    println("VERIFY ERROR: tar.gz file should NOT exist in local repository but was found at: " + localRepoArtifact.absolutePath)
    return false
}
println("SUCCESS: tar.gz was correctly detached and NOT installed to local repository")

// Verify the main jar WAS installed (to ensure install phase ran)
File mainJar = new File(localRepoBase, "org/wildfly/plugins/test/detach-tar-test/${version}/detach-tar-test-${version}.jar")
if (!mainJar.exists()) {
    println("VERIFY ERROR: main jar should exist in local repository but was not found at: " + mainJar.absolutePath)
    return false
}
println("SUCCESS: Main jar was correctly installed to local repository")

// Check if signature file was created (only if gpg.skip was false)
File tarGzAscFile = new File(targetDir, tarGzFileName + ".asc")
if (tarGzAscFile.exists()) {
    println("INFO: Signature file was created: " + tarGzAscFile.absolutePath)
    
    // Verify the .asc file was NOT installed to the local repository
    File localRepoAscArtifact = new File(localRepoBase, "org/wildfly/plugins/test/detach-tar-test/${version}/detach-tar-test-${version}-test.tar.gz.asc")
    if (localRepoAscArtifact.exists()) {
        println("VERIFY ERROR: .asc file should NOT exist in local repository but was found at: " + localRepoAscArtifact.absolutePath)
        return false
    }
    println("SUCCESS: .asc signature file was correctly detached and NOT installed to local repository")
} else {
    println("INFO: Signature file was not created (gpg.skip=true)")
}

println("VERIFY SUCCESS: All checks passed!")
return true
