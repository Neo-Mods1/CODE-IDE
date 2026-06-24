# ProGuard rules for CODE-IDE
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.neo.ide.** { *; }
