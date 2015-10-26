mkdir conf/src/test/java/com/mucommander/commons
mkdir file/src/test/java/com/mucommander/commons
mkdir io/src/test/java/com/mucommander/commons
mkdir runtime/src/test/java/com/mucommander/commons
mkdir util/src/test/java/com/mucommander/commons
mkdir collections/src/test/java/com/mucommander/commons
mkdir manager/src/test/java

mkdir conf/src/main/java/com/mucommander/commons
mkdir file/src/main/java/com/mucommander/commons
mkdir io/src/main/java/com/mucommander/commons
mkdir runtime/src/main/java/com/mucommander/commons
mkdir util/src/main/java/com/mucommander/commons
mkdir collections/src/main/java/com/mucommander/commons
mkdir manager/src/main/java


git mv src/test/com/mucommander/commons/conf          conf/src/test/java/com/mucommander/commons
git mv src/test/com/mucommander/commons/file          file/src/test/java/com/mucommander/commons
git mv src/test/com/mucommander/commons/io            io/src/test/java/com/mucommander/commons
git mv src/test/com/mucommander/commons/runtime       runtime/src/test/java/com/mucommander/commons
git mv src/test/com/mucommander/commons/util          util/src/test/java/com/mucommander/commons
git mv src/test/com/mucommander/commons/collections   collections/src/test/java/com/mucommander/commons
git mv src/test/*                                     manager/src/test/java


git mv src/main/com/mucommander/commons/conf          conf/src/main/java/com/mucommander/commons
git mv src/main/com/mucommander/commons/file          file/src/main/java/com/mucommander/commons
git mv src/main/com/mucommander/commons/io            io/src/main/java/com/mucommander/commons
git mv src/main/com/mucommander/commons/runtime       runtime/src/main/java/com/mucommander/commons
git mv src/main/com/mucommander/commons/util          util/src/main/java/com/mucommander/commons
git mv src/main/com/mucommander/commons/collections   collections/src/main/java/com/mucommander/commons
git mv src/main/*                                     manager/src/main/java

mkdir file/src/main/java/ru/trolsoft/utils
git mv manager/src/main/java/ru/trolsoft/utils/FileUtils.java file/src/main/java/ru/trolsoft/utils/FileUtils.java
git mv manager/src/main/java/ru/trolsoft/utils/StrUtils.java file/src/main/java/ru/trolsoft/utils/StrUtils.java

mkdir file/src/main/java/com/mucommander/core/
git mv manager/src/main/java/com/mucommander/core/FolderChangeMonitor.java file/src/main/java/com/mucommander/core/FolderChangeMonitor.java

mkdir file/src/main/resources
git mv res/runtime/com file/src/main/resources


mkdir manager/src/main
git mv res/runtime res/resources
git mv res/resources manager/src/main

mkdir manager/src/main
git mv res/package res/assembly
git mv res/assembly manager/src/main
