#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "opencv2/highgui/highgui.hpp"
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/objdetect.hpp>
#include <vector>

using namespace std;
using namespace cv;

extern "C" JNIEXPORT jstring JNICALL
Java_com_aim_pesame_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_aim_pesame_MainActivity_FindPeople(JNIEnv*, jobject, jlong addrGray, jlong addrRgba) {
    Mat &mGr = *(Mat *) addrGray;
    Mat &mRgb = *(Mat *) addrRgba;
    /*vector<KeyPoint> v;
    Ptr<FeatureDetector> detector = FastFeatureDetector::create(50);
    detector->detect(mGr, v);
    for (unsigned int i = 0; i < v.size(); i++) {
        const KeyPoint& kp = v[i];
        circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255,0,0,255));
    }*/
    //
    Mat foundWeights;
    cv::HOGDescriptor hog;
    hog.setSVMDetector(cv::HOGDescriptor::getDefaultPeopleDetector());
    vector<cv::Rect> rects;
    cv::equalizeHist(mGr, mGr);
    //InputArray img, CV_OUT std::vector<Rect>& foundLocations,
    //                                  Mat hitThreshold = 0, Size winStride = Size(),
    //                                  Size padding = Size(), double scale = 1.05,
    //                                  double finalThreshold = 2.0, bool useMeanshiftGrouping = false
    hog.detectMultiScale(mGr, rects,0, cv::Size(8, 8), cv::Size(32, 32),1.05, 2,false);
    for (unsigned int i=0;i<rects.size();i++) {
        cv::rectangle(mRgb, cv::Point(rects[i].x, rects[i].y),
                      cv::Point(rects[i].x+rects[i].width, rects[i].y+rects[i].height),
                      cv::Scalar(255, 0, 255));
    }

    //

}