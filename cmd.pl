#!/usr/bin/perl
use Cwd;
$ENV{PATH}="$ENV{PATH}:$ENV{HOME}/Library/Android/sdk/platform-tools/";
$app="com.alexeilebedev.gles2";
while ($command=shift) {
    docmd($command);
}
sub docmd($) {
    my $command=$_[0];
    if ($command eq "start") {
	system("adb shell am start -a android.intent.action.MAIN -n $app/.Home -S");
    } elsif ($command eq "stop") {
	system("adb shell am force-stop $app");
    } elsif ($command eq "reinstall") {
	docmd("stop");
	print "uninstalling\n";
	system("adb -d uninstall $app");
	print "installing\n";
	system("adb -d install ./app/build/outputs/apk/debug/app-debug.apk");
    } elsif ($command eq "build") {
	gradlecmd("installDebug");
    } else {
	print "bad command [$command]\n";
    }
}
sub syscmd {
    print join(" ",@_),"\n";
    system(@_);
}
sub gradlecmd($) {
    my $cmd=$_[0];
    $APP_HOME=getcwd;
    $classpath="$APP_HOME/gradle/wrapper/gradle-wrapper.jar";
    $ENV{CLASSPATH}="$classpath";
    $ENV{GRADLE_OPTS}="\"-Xdock:name=Gradle\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\"";
    syscmd("java", "-Dorg.gradle.appname=gradlew", "-classpath", "$ENV{CLASSPATH}", "org.gradle.wrapper.GradleWrapperMain", $cmd);
}
