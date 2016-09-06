//
// Created by luotianqiang1 on 16/9/6.
//

#ifndef ADS_DEMO_ADSMANAGER_H
#define ADS_DEMO_ADSMANAGER_H


class AdsManager {
private:
    static AdsManager* instance = nullptr;
public:
    static AdsManager* getInstance(){
        if(instance == nullptr)
            instance = new AdsManager();
        return instance;
    }
};


#endif //ADS_DEMO_ADSMANAGER_H
