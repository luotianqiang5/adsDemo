//
// Created by luotianqiang1 on 16/9/6.
//

#include "com_common_ads_AdsManager.h"


/*
 * Class:     com_common_ads_AdsManager
 * Method:    onRewarded
 * Signature: (ILjava/lang/String;IZ)V
 */
JNIEXPORT void JNICALL Java_com_common_ads_AdsManager_onRewarded
        (JNIEnv *, jobject, jint, jstring, jint, jboolean){

}

/*
 * Class:     com_common_ads_AdsManager
 * Method:    onLoadedSuccess
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_common_ads_AdsManager_onLoadedSuccess
        (JNIEnv *, jobject, jint){

}

/*
 * Class:     com_common_ads_AdsManager
 * Method:    onLoadedFail
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_common_ads_AdsManager_onLoadedFail
        (JNIEnv *, jobject, jint);

/*
 * Class:     com_common_ads_AdsManager
 * Method:    onAdsOpened
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_common_ads_AdsManager_onAdsOpened
        (JNIEnv *, jobject, jint);

/*
 * Class:     com_common_ads_AdsManager
 * Method:    onAdsClosed
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_common_ads_AdsManager_onAdsClosed
        (JNIEnv *, jobject, jint){

}

/*
 * Class:     com_common_ads_AdsManager
 * Method:    onAdsClicked
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_common_ads_AdsManager_onAdsClicked
        (JNIEnv *, jobject, jint){

}

/*
 * Class:     com_common_ads_AdsManager
 * Method:    onOtherEvent
 * Signature: (ILjava/lang/String;[Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_common_ads_AdsManager_onOtherEvent
        (JNIEnv *, jobject, jint, jstring, jobjectArray){

}