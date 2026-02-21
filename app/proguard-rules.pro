# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# R8 renames FlowParameters, causing ClassNotFoundException when Android restores
# AuthMethodPickerActivity from the back stack after process death. The Parcel
# stores the original class name, which no longer exists after obfuscation, so
# getParcelableExtra() returns null and AppCompatBase.onCreate() crashes on null.themeId.
# https://github.com/firebase/FirebaseUI-Android/issues/689
# https://github.com/firebase/FirebaseUI-Android/issues/765
# https://github.com/firebase/FirebaseUI-Android/issues/1416
-keep class com.firebase.ui.auth.data.model.** { *; }

-keep class org.apache.logging.log4j.message.**
-keep class org.slf4j.**
-keep class org.apache.commons.compress.archivers.zip.**
-keep class org.openxmlformats.schemas.** { *; }
-keep class org.apache.xerces.**
-keep class org.apache.poi.**
-keep class javax.xml.transform.** { *; }
-keep class javax.xml.transform.dom.** { *; }
-keep class javax.xml.parsers.** { *; }
-keep class org.w3c.dom.** { *; }
-keep class org.xml.sax.** { *; }


-dontwarn java.beans.ConstructorProperties
-dontwarn javax.xml.crypto.**
-dontwarn javax.xml.stream.**
-dontwarn javax.xml.transform.stax.**
-dontwarn org.apache.batik.**
-dontwarn org.apache.poi.java.awt.**
-dontwarn org.apache.xml.**
-dontwarn org.openxmlformats.schemas.**
-dontwarn org.w3c.dom.**
-dontwarn org.osgi.framework.Bundle
-dontwarn org.osgi.framework.BundleContext
-dontwarn org.osgi.framework.FrameworkUtil
-dontwarn org.osgi.framework.ServiceReference
-dontwarn net.sf.saxon.**
-dontwarn de.rototor.pdfbox.graphics2d.PdfBoxGraphics2DFontTextDrawer
-dontwarn org.apache.pdfbox.pdmodel.PDDocument
