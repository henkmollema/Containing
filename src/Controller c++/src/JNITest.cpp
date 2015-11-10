#include <jni.h>
#include <iostream>
#include "JNITest.h"

using namespace std;

JNIEXPORT void JNICALL Java_controller_JNITest_helloFromC(JNIEnv *, jobject)
{
    cout << "hello" << endl;
}