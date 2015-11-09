#include <jni.h>
#include <iostream>
#include "JNI.h"

using namespace std;

JNIEXPORT void JNICALL Java_controller_JNI_helloFromC(JNIEnv *, jobject)
{
    cout << "hello" << endl;
}