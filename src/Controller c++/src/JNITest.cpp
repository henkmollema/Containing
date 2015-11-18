#include <iostream>
#include"vector2.h"
#include"road_map.h"
#include"node.h"
#include "JNITest.h"

using namespace std;

road_map* roadmap;

JNIEXPORT void JNICALL Java_controller_JNITest_initPath(JNIEnv *, jclass)
{
    roadmap = new road_map({
		road_map::node_base(vector2(0.0f, 0.0f),{ 1, 2 }),
		road_map::node_base(vector2(1.0f, 0.0f),{ 0, 2 }),
		road_map::node_base(vector2(1.0f, 1.0f),{ 0, 1, 3, 4 }),
		road_map::node_base(vector2(2.0f, 2.0f),{ 2, 4 }),
		road_map::node_base(vector2(0.0f, 4.0f),{ 2, 3 })
    });
}

vector<int> getPath(int from, int to, float speed)
{
    return roadmap->get_path(from, to, speed);
}

JNIEXPORT jintArray JNICALL Java_controller_JNITest_getPath(JNIEnv *env, jclass, jint from, jint to, jfloat speed)
{
    vector<int> tempVec = getPath(from, to, speed);
    jintArray res = env->NewIntArray(tempVec.size());
    env->SetIntArrayRegion(res, 0, tempVec.size(), &tempVec[0]);
    return res;
}

/*
JNIEXPORT void JNICALL Java_controller_JNITest_helloFromC(JNIEnv *, jclass )
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
    jdouble avg = (jdouble)sum / (jdouble)length;
    env->ReleaseIntArrayElements(arr, inCArray, 0);
    return avg;
}

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
*/
JNIEXPORT void JNICALL Java_controller_JNITest_cleanup(JNIEnv *, jclass)
{
    delete roadmap;
}