#include <jni.h>
#include <iostream>
#include "JNITest.h"

using namespace std;

JNIEXPORT void JNICALL Java_controller_JNITest_helloFromC(JNIEnv *, jclass)
{
    cout << "hello" << endl;
}

JNIEXPORT jdouble JNICALL Java_controller_JNITest_avgFromC(JNIEnv *env, jclass, jintArray arr)
{
    jint *inCArray = env->GetIntArrayElements(arr, NULL);
    jsize length = env->GetArrayLength(arr);
    jint sum = 0;
    for (int i = 0; i < length; i++)
    {
        sum += inCArray[i];
    }
    //cout << "sum: " << sum << endl;
    //cout << "length: " << length << endl;
    jdouble avg = (jdouble)avg / (jdouble)length;
    //cout << "avg " << avg << endl;
    env->ReleaseIntArrayElements(arr, inCArray, 0);
    return avg;
}
/**/
JNIEXPORT jint JNICALL Java_controller_JNITest_intFromC(JNIEnv *env, jclass thisCls, jintArray arr)
{
    jmethodID methId = env->GetStaticMethodID(thisCls, "avgFromC", "([I)D");
    if (env->ExceptionCheck()) return 0;
    jint res = env->CallStaticDoubleMethod(thisCls, methId, arr);
    return res;
}

JNIEXPORT jobject JNICALL Java_controller_JNITest_integerFromC(JNIEnv *env, jclass, jint x)
{
    jclass cls = env->FindClass("java/lang/Integer");
    if (env->ExceptionCheck()) return NULL;
    jmethodID methodId = env->GetMethodID(cls, "<init>", "(I)V");
    if (env->ExceptionCheck()) return NULL;
    jobject obj = env->NewObject(cls, methodId, x);
    return obj;
}

JNIEXPORT jobject JNICALL Java_controller_JNITest_pointInC(JNIEnv *env, jclass, jint x, jint y)
{
    jclass cls = env->FindClass("java/awt/Point");
    if (env->ExceptionCheck()) return NULL;
    jmethodID methodId = env->GetMethodID(cls, "<init>", "(II)V");
    if (env->ExceptionCheck()) return NULL;
    jobject obj = env->NewObject(cls, methodId, x, y);
    return obj;
}

JNIEXPORT void JNICALL Java_controller_JNITest_changeNumberInC(JNIEnv *env, jobject thisObj)
{
    jclass thisClass = env->GetObjectClass(thisObj);
    if (env->ExceptionCheck()) return;
    jfieldID fidNumber = env->GetFieldID(thisClass, "number", "I");
    if (env->ExceptionCheck()) return;
    jint number = env->GetIntField(thisObj, fidNumber);
    cout << "In C, the int is " << number << endl;
    number = 99;
    env->SetIntField(thisObj, fidNumber, number);
}