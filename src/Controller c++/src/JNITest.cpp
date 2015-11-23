#include <iostream>
#include"vector2.h"
#include"road_map.h"
#include"node.h"
#include "JNITest.h"

using namespace std;

road_map* roadmap;

JNIEXPORT void JNICALL Java_controller_JNITest_initPath(JNIEnv *env, jclass, jobject dimension)
{
    vector<road_map::node_base> temp = { };
    jclass dimensionCls = env->FindClass("java/awt/Dimension");
    if (env->ExceptionCheck()) return;
    jfieldID dimensionXField = env->GetFieldID(dimensionCls, "width", "I");
    jfieldID dimensionYField = env->GetFieldID(dimensionCls, "height", "I");
    if (env->ExceptionCheck()) return;
    int dimensionX = env->GetIntField(dimension, dimensionXField);
    int dimensionY = env->GetIntField(dimension, dimensionYField);
    long dinges = 0;
    for (int i = 0; i < dimensionX; i++)
    {
        for (int j = 0; j < dimensionY; j++)
        {
            vector<int> conn = vector<int>(0);
            if (i > 0) conn.push_back(((i - 1) * dimensionY) + j);
            if (j > 0) conn.push_back(i * dimensionY + (j - 1));
            if (i < dimensionX - 1) conn.push_back(((i + 1) * dimensionY) + j);
            if (j < dimensionY - 1) conn.push_back(i * dimensionY + (j + 1));
            temp.push_back(road_map::node_base(vector2((float)i, (float)j), conn));
            dinges++;
        }
    }
    roadmap = new road_map(temp);
}

vector<int> getPath(int from, int to, float speed)
{
    return roadmap->get_path(from, to, speed);
}

void throw_java_exception(JNIEnv *env, char *className, char *message)
{
    jclass ex = env->FindClass(className);
    env->ThrowNew(ex, message);
}

JNIEXPORT jintArray JNICALL Java_controller_JNITest_getPath(JNIEnv *env, jclass, jint from, jint to, jfloat speed)
{
    if (from < 0 || from >= roadmap->size())
    {
        char* className = "java/lang/IllegalArgumentException";
        char* message = "origin is not in the roadmap";
        throw_java_exception(env, className, message);
        return NULL;
    }
    if (to < 0 || to >= roadmap->size())
    {
        char* className = "java/lang/IllegalArgumentException";
        char* message = "destination is not in the roadmap";
        throw_java_exception(env, className, message);
        return NULL;
    }
    vector<int> tempVec = getPath(from, to, speed);
    jint temp[tempVec.size()];
    jintArray res = env->NewIntArray(tempVec.size());
    for (int i = 0; i < tempVec.size(); i++)
    {
        temp[i] = tempVec[i];
    }
    env->SetIntArrayRegion(res, 0, tempVec.size(), temp);
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