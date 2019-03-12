//
//  concurrency.cpp
//  N-queen
//
//  Created by TNT on 2018/11/11.
//  Copyright © 2018年 TNT. All rights reserved.
//

#include <stdlib.h>
#include <string.h>
#include <sys/time.h>

#define MAXN 100
#define true 1
#define false 0
using name
struct Result {
    int xUsed;
    int count;
};
inline int abs(int a) {
    return a >= 0 ? a : (-a);
}

int n;
__thread Result *re;
__thread int P[MAXN],used[MAXN];

void generateP(int index/*,int *pCount,int *P,int* used*/) {
    int flag;
    if(index == n + 1) {
        re->count++;
        return;
    }
    for(int x = 1; x <= n; x++) {
        if(used[x] == false) {
            flag = true;
            for(int pre = 1; pre< index; pre++) {
                if(abs(index - pre) == abs(x - P[pre])) {
                    flag = false;
                    break;
                }
            }
            if(flag) {
                P[index] = x;
                used[x] = true;
                generateP(index + 1/*,pCount,P,used*/);
                used[x] = false;
            }
        }
    }
}

void thrFun(void *arg) {
    
    struct timeval start,end;
    long long startusec,endusec;
    re = (Result*)arg;
    // __thread int P[MAXN],used[MAXN];
    memset(P,0,sizeof(int)*MAXN);
    memset(used,0,sizeof(int)*MAXN);
    P[1] = re->xUsed;
    used[re->xUsed] = true;
    
    gettimeofday(&start,NULL);
    generateP(2/*,&(re->count),P,used*/);
    gettimeofday(&end,NULL);
    startusec = start.tv_sec * 1000000 + start.tv_usec;
    endusec = end.tv_sec * 1000000 + end.tv_usec;
    printf("spend %lld us\n", (endusec - startusec));
}


int main(int argc,char **argv)
{
    if(argc != 3) {
        fprintf(stderr,"error args\n");
        return -1;
    }
    int nThr;//工作线程数
    int sum = 0;
    struct timeval start,end;
    n = atoi(argv[1]);
    nThr = atoi(argv[2]);
    long long startusec,endusec;
    Result *reArray = (Result*)malloc(sizeof(Result)*n);
    memset(reArray,0,sizeof(Result)*n);
    
    //    gettimeofday(&start,NULL);
    //
    //    {
    //        concurrency tp(nThr);
    //        for(int i = 0; i < n; i++) {
    //            reArray[i] = {i + 1,0};
    //            tp.addTask(thrFun,(void*)(&reArray[i]));
    //        }
    //    }
    for(int i = 0; i < n; i++) {
        sum += reArray[i].count;
    }
    gettimeofday(&end,NULL);
    
    cout<<"result="<<sum;
    startusec = start.tv_sec * 1000000 + start.tv_usec;
    endusec = end.tv_sec * 1000000 + end.tv_usec;
    printf("spend %lld us\n", (endusec - startusec));
    return 0;
}
