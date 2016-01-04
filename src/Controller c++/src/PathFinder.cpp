#include <iostream>
#include "vector2.h"
#include "road_map.h"
#include "node.h"
#include "nhl_containing_controller_PathFinder.h"

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
JNIEXPORT void JNICALL Java_nhl_containing_controller_PathFinder_initPath__Ljava_awt_Dimension_2(JNIEnv *env, jclass, jobject dimension)
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
    int size = dimensionX/5;
    for (int i = 0; i < dimensionX; i++)
    {
        for (int j = 0; j < dimensionY; j++)
        {
            vector<int> conn = vector<int>(0);
            if ((i < dimensionX/2-size || i > dimensionX/2+size) || (j < dimensionY/2-size || j > dimensionY/2+size))
            {
                if (i > 0) conn.push_back(((i - 1) * dimensionY) + j);
                if (j > 0) conn.push_back(i * dimensionY + (j - 1));
                if (i < dimensionX - 1) conn.push_back(((i + 1) * dimensionY) + j);
                if (j < dimensionY - 1) conn.push_back(i * dimensionY + (j + 1));
                //cout << "connecties van node " << i*dimensionY+j << " aangemaakt" << endl;
            }
            temp.push_back(road_map::node_base(vector2((float)i - dimensionX/2, (float)j- dimensionY/2), conn));
        }
    }
    roadmap = new road_map(temp);
}

/*
 * Class:     nhl_containing_controller_PathFinder
 * Method:    initPath
 * Signature: ([Lnhl/containing/controller/simulation/Node;)V
 */
JNIEXPORT void JNICALL Java_nhl_containing_controller_PathFinder_initPath___3Lnhl_containing_controller_simulation_Node_2(JNIEnv *env, jclass, jobjectArray nodesArr)
{
    vector<road_map::node_base> temp = { };
    jclass nodeClass = NULL;
    jclass vectorClass = env->FindClass("nhl/containing/controller/Vector2f");
    if (env->ExceptionCheck()) return;
    jfieldID idField;
    jfieldID positionField;
    jfieldID connectionField;
    jfieldID positionxField = NULL;
    jfieldID positionyField = NULL;
    jsize length = env->GetArrayLength(nodesArr);
    for (int i = 0; i < length; i++)
    {
        jobject node = env->GetObjectArrayElement(nodesArr, i);
        if (nodeClass == NULL)
        {
            nodeClass = env->GetObjectClass(node);
            idField = env->GetFieldID(nodeClass, "m_id", "I");
            positionField = env->GetFieldID(nodeClass, "m_position", "Lnhl/containing/controller/Vector2f;");
            connectionField = env->GetFieldID(nodeClass, "m_connections", "[I");
            if (env->ExceptionCheck()) return;
        }
        vector<int> conn = vector<int>(0);
        jobject position = env->GetObjectField(node, positionField);
        if (positionxField == NULL)
        {
            positionxField = env->GetFieldID(vectorClass, "x", "F");
            positionyField = env->GetFieldID(vectorClass, "y", "F");
            if (env->ExceptionCheck()) return;
        }
        jobject connection = env->GetObjectField(node, connectionField);
        jintArray* arr = reinterpret_cast<jintArray*>(&connection);
        jsize length = env->GetArrayLength(*arr);
        jint* inConn = env->GetIntArrayElements(*arr, NULL);
        for (char i = 0; i < length; i++)
        {
            conn.push_back(inConn[i]);
        }
        temp.push_back(road_map::node_base(vector2(env->GetFloatField(position, positionxField), env->GetFloatField(position, positionyField)), conn));
        env->ReleaseIntArrayElements(*arr, inConn, 0);
    }
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

/*
 * Class:     nhl_containing_controller_PathFinder
 * Method:    makeConnection
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_nhl_containing_controller_PathFinder_makeConnection__II(JNIEnv *, jclass, jint orgId, jint destId)
{
    
}

/*
 * Class:     nhl_containing_controller_PathFinder
 * Method:    makeConnection
 * Signature: (Ljava/awt/Point;Ljava/awt/Point;)V
 */
JNIEXPORT void JNICALL Java_nhl_containing_controller_PathFinder_makeConnection__Ljava_awt_Point_2Ljava_awt_Point_2(JNIEnv *env, jclass, jobject org, jobject dest)
{
    if (org == NULL || dest == NULL)
    {
        string className = "java/lang/IllegalArgumentException";
        string message = "dimension can't be null";
        throw_java_exception(env, &className[0], &message[0]);
        return;
    }
    jclass pointCls = env->GetObjectClass(org);
    jfieldID pointXField = env->GetFieldID(pointCls, "width", "I");
    jfieldID pointYField = env->GetFieldID(pointCls, "height", "I");
    if (env->ExceptionCheck()) return;
    int orgId = env->GetIntField(org, pointXField) * sqrt(roadmap->size()) + env->GetIntField(org, pointYField);
    int destId = env->GetIntField(dest, pointXField) * sqrt(roadmap->size()) + env->GetIntField(dest, pointYField);
    
}

/*
 * Class:     nhl_containing_controller_PathFinder
 * Method:    makeConnection
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_nhl_containing_controller_PathFinder_makeConnection__IIII(JNIEnv *, jclass, jint orgx, jint orgy, jint destx, jint desty)
{
    int orgId  = orgx*sqrt(roadmap->size())+orgy;
    int destId = destx*sqrt(roadmap->size())+desty;
    
}

/*
 * Class:     nhl_containing_controller_PathFinder
 * Method:    setOccupied
 * Signature: (Ljava/awt/Point;Z)V
 */
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
    roadmap->set_occupied(pointX, pointY, occ);
}

/*
 * Class:     nhl_containing_controller_PathFinder
 * Method:    getOccupied
 * Signature: (Ljava/awt/Point;)Z
 */
JNIEXPORT jboolean JNICALL Java_nhl_containing_controller_PathFinder_getOccupied(JNIEnv *env, jclass, jobject point)
{
    jclass pointCls = env->GetObjectClass(point);
    if (env->ExceptionCheck()) return false;
    jfieldID pointXField = env->GetFieldID(pointCls, "x", "I");
    jfieldID pointYField = env->GetFieldID(pointCls, "y", "I");
    if (env->ExceptionCheck()) return false;
    int pointX = env->GetIntField(point, pointXField);
    int pointY = env->GetIntField(point, pointYField);
    return roadmap->get_occupied(pointX, pointY);
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