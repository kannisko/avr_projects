/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class ioPort */

#ifndef _Included_ioPort
#define _Included_ioPort
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     ioPort
 * Method:    Out32
 * Signature: (SS)V
 */
JNIEXPORT void JNICALL Java_jnpout32_ioPort_Out32
  (JNIEnv *, jobject, jshort PortAddress, jshort data);
/*
 * Class:     ioPort
 * Method:    Inp32
 * Signature: (S)S
 */
JNIEXPORT jshort JNICALL Java_jnpout32_ioPort_Inp32
  (JNIEnv *, jobject, jshort PortAddress);

#ifdef __cplusplus
}
#endif
#endif
