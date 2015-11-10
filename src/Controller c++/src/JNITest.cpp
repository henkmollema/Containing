#include <jni.h>
#include <iostream>
#include "JNITest.h"

using namespace std;

JNIEXPORT void JNICALL Java_controller_JNITest_helloFromC(JNIEnv *, jclass)
{
    cout << "hello" << endl;
}

JNIEXPORT void JNICALL Java_controller_JNITest_helloFromC__Ljava_lang_String_2(JNIEnv *, jclass, jstring)
{
    
}