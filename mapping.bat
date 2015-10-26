rem move tests
git mv src\test                                     src\java
git mv src\java                                     manager\src\test


mkdir conf\src\main\java\com\mucommander\commons
mkdir io\src\main\java\com\mucommander\commons

git mv src\main\com\mucommander\conf          conf\src\main\java\com\mucommander
git mv src\main\com\mucommander\io            io\src\main\java\com\mucommander

mkdir src2
git mv src\main                               src2\java
git mv src2\java                             manager\src\main
