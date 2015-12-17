#include <iostream>
#include"vector2.h"
#include"road_map.h"
#include"node.h"
#include "PathFinder.h"

using namespace std;

road_map* roadmap;

void throw_java_exception(JNIEnv *env, char *className, char *message)
{
    jclass ex = env->FindClass(className);
    env->ThrowNew(ex, message);
}

/*
 * Class:     nhl_containing_controller_PathFinder
 * Method:    initPath
 * Signature: (Ljava/awt/Dimension;)V
 */
JNIEXPORT void JNICALL Java_nhl_containing_controller_PathFinder_initPath(JNIEnv *env, jclass, jobject dimension)
{
    vector<road_map::node_base> temp = { };
    if (dimension == NULL)
    {
        string className = "java/lang/IllegalArgumentException";
        string message = "dimension can't be null";
        throw_java_exception(env, &className[0], &message[0]);
        return;
    }
    jclass dimensionCls = env->GetObjectClass(dimension);
    jfieldID dimensionXField = env->GetFieldID(dimensionCls, "width", "I");
    jfieldID dimensionYField = env->GetFieldID(dimensionCls, "height", "I");
    if (env->ExceptionCheck()) return;
    int dimensionX = env->GetIntField(dimension, dimensionXField);
    int dimensionY = env->GetIntField(dimension, dimensionYField);
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
        }
    }
    roadmap = new road_map(temp);
}

vector<int> getPath(int from, int to, float speed)
{
    return roadmap->get_path(from, to, speed);
}

/*
 * Class:     nhl_containing_controller_PathFinder
 * Method:    getPath
 * Signature: (IIF)[I
 */
JNIEXPORT jintArray JNICALL Java_nhl_containing_controller_PathFinder_getPath(JNIEnv *env, jclass, jint from, jint to, jfloat speed)
{
    if (from < 0 || from >= roadmap->size())
    {
        string className = "java/lang/IllegalArgumentException";
        string message = "origin is not in the roadmap";
        throw_java_exception(env, &className[0], &message[0]);
        return NULL;
    }
    if (to < 0 || to >= roadmap->size())
    {
        string className = "java/lang/IllegalArgumentException";
        string message = "destination is not in the roadmap";
        throw_java_exception(env, &className[0], &message[0]);
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

JNIEXPORT void JNICALL Java_nhl_containing_controller_PathFinder_setOccupied(JNIEnv *env, jclass, jobject point, jboolean occ)
{
    if (point == NULL)
    {
        string className = "java/lang/IllegalArgumentException";
        string message = "point can't be null";
        throw_java_exception(env, &className[0], &message[0]);
        return;
    }
    jclass pointCls = env->GetObjectClass(point);
    jfieldID pointXField = env->GetFieldID(pointCls, "x", "I");
    jfieldID pointYField = env->GetFieldID(pointCls, "y", "I");
    if (env->ExceptionCheck()) return;
    int pointX = env->GetIntField(point, pointXField);
    int pointY = env->GetIntField(point, pointYField);
    string className = "java/lang/NotImplementedException";
    string message = "Not supported yet.";
    throw_java_exception(env, &className[0], &message[0]);
    return;
}

JNIEXPORT jboolean JNICALL Java_nhl_containing_controller_PathFinder_getOccupied(JNIEnv *env, jclass, jobject point)
{
    jclass pointCls = env->GetObjectClass(point);
    if (env->ExceptionCheck()) return false;
    jfieldID pointXField = env->GetFieldID(pointCls, "x", "I");
    jfieldID pointYField = env->GetFieldID(pointCls, "y", "I");
    if (env->ExceptionCheck()) return false;
    int pointX = env->GetIntField(point, pointXField);
    int pointY = env->GetIntField(point, pointYField);
    string className = "java/lang/NotImplementedException";
    string message = "Not supported yet.";
    throw_java_exception(env, &className[0], &message[0]);
    return false;
}
/*
JNIEXPORT void JNICALL Java_nhl_containing_controller_PathFinder_helloFromC(JNIEnv *, jclass )
{
    cout << "hello" << endl;
}

JNIEXPORT jdouble JNICALL Java_nhl_containing_controller_PathFinder_avgFromC(JNIEnv *env, jclass, jintArray arr)
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

JNIEXPORT jint JNICALL Java_nhl_containing_controller_PathFinder_intFromC(JNIEnv *env, jclass thisCls, jintArray arr)
{
    jmethodID methId = env->GetStaticMethodID(thisCls, "avgFromC", "([I)D");
    if (env->ExceptionCheck()) return 0;
    jint res = env->CallStaticDoubleMethod(thisCls, methId, arr);
    return res;
}

JNIEXPORT jobject JNICALL Java_nhl_containing_controller_PathFinder_integerFromC(JNIEnv *env, jclass, jint x)
{
    jclass cls = env->FindClass("java/lang/Integer");
    if (env->ExceptionCheck()) return NULL;
    jmethodID methodId = env->GetMethodID(cls, "<init>", "(I)V");
    if (env->ExceptionCheck()) return NULL;
    jobject obj = env->NewObject(cls, methodId, x);
    return obj;
}

JNIEXPORT jobject JNICALL Java_nhl_containing_controller_PathFinder_pointInC(JNIEnv *env, jclass, jint x, jint y)
{
    jclass cls = env->FindClass("java/awt/Point");
    if (env->ExceptionCheck()) return NULL;
    jmethodID methodId = env->GetMethodID(cls, "<init>", "(II)V");
    if (env->ExceptionCheck()) return NULL;
    jobject obj = env->NewObject(cls, methodId, x, y);
    return obj;
}

JNIEXPORT void JNICALL Java_nhl_containing_controller_PathFinder_changeNumberInC(JNIEnv *env, jobject thisObj)
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
/*
 * Class:     nhl_containing_controller_PathFinder
 * Method:    cleanup
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_nhl_containing_controller_PathFinder_cleanup(JNIEnv *, jclass)
{
    delete roadmap;
}