#Some Tips

## FileTemplates
### Java
/**
 * @Description:
 * @CreateTime: ${DATE} ${TIME}
 * @Site: shmily-qjj.top
 * @author 佳境Shmily
 */


 
### Scala
/**
* @Description:
* @CreateTime: ${DATE} ${TIME}
* @Site: shmily-qjj.top
* @author 佳境Shmily
  */
 
### Python
```
#!/usr/bin/env python
# encoding: utf-8
"""
:Description:
:Author: 佳境Shmily
:Create Time: ${DATE} ${TIME}
:File: ${NAME}
:Site: shmily-qjj.top
"""
```

## Git
### Remove files or folders in remote repositories
git rm -r --cached path
git commit -m "rm-files"
git push origin master

## IDEA Plugins
Alibaba Java Coding Guidelines
Big Data Tool
Key Promoter X
Maven Helper
Python
Scala plugin
Docker
Codeglance
Nyan Progress Bar
Rainbow Brackets
Translation
Easy Code
Leetcode Editor
GitToolBox
arthas idea
Antlr
Go
MybatisX
Lombok
GitToolBox
Big Data Tools

## IDEA Optimize
1.vmoptions
```text
-Xms4096m
-Xmx8192m
-XX:ReservedCodeCacheSize=512m
-XX:MetaspaceSize=250m
-XX:+IgnoreUnrecognizedVMOptions
-XX:+UseG1GC
-XX:SoftRefLRUPolicyMSPerMB=50
-XX:CICompilerCount=2
-XX:+HeapDumpOnOutOfMemoryError
-XX:-OmitStackTraceInFastThrow
-ea
-Dsun.io.useCanonCaches=false
-Djdk.http.auth.tunneling.disabledSchemes=""
-Djdk.attach.allowAttachSelf=true
-Djdk.module.illegalAccess.silent=true
-Dkotlinx.coroutines.debug=off
-Xverify:none
-XX:ErrorFile=$USER_HOME/java_error_in_idea_%p.log
-XX:HeapDumpPath=$USER_HOME/java_error_in_idea.hprof
-Dfile.encoding=UTF-8
```

2.idea.properties(位于安装目录)
idea.max.intellisense.filesize=2500改为25000

3.编译进程和Maven内存调大
File | Settings | Build, Execution, Deployment | Compiler 调整Shared Build Process Heap Size为4096M
File | Settings | Build, Execution, Deployment | Build Tools | Maven | Importing 调整Vm options for importer为-Xmx4096m 
