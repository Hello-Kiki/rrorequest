# RRODemo

    对retrofit2+rxjava2+okhttp3 进行封装使用，尽可能方便的调用
    封装功能：普通请求（post,get）
              文件上传（图文，多文件，单文件带进度）
              文件下载（可多文件同时，带进度）


## 普通请求调用

           HttpManager.getInstance().create(ApiService.class).getData()
                    .compose(HttpManager.<JsonObject>applySchedulers())
                    .subscribe(new SimpleCallBack<JsonObject>() {
                        @Override
                        public void onSuccess(JsonObject jsonObject) {
                            Log.e("2017","成功-->"+jsonObject.toString());
                            mTextView.setText(jsonObject.toString());
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            Log.e("2017","失败-->"+e.toString());
                        }
                    });


## 文件上传

         Map<String,RequestBody> textBody=MultipartUtil.newInstance()
                        .addParam("text1","123")
                        .addParam("text2","456")
                        .Build();

                List<File> files=new ArrayList<>();
                File file=new File(Environment.getExternalStorageDirectory()+"test.png");
                files.add(file);

                //文件上传进度只支持单文件上传的时候使用
                 List<MultipartBody.Part> parts= MultipartUtil.makeMultpart("images", files, new ProgressListener() {
                  @Override
                   public void onProgress(long read, long length, boolean done) {

                    }
                  });
                HttpManager.getInstance().create(ApiService.class).uploadFile(textBody,parts)
                        .compose(HttpManager.<JsonObject>applySchedulers())
                        .subscribe(new SimpleCallBack<JsonObject>() {
                            @Override
                            public void onSuccess(JsonObject jsonObject) {
                                //请求成功
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                //请求失败
                            }
                        });

 ## 文件下载

       因为每个人使用的数据库都不一样，在这里不统一做下载数据的保存，下载信息类为  DownInfo  ,如果需要做保存，自行继承处理。

        private DownInfo mDownInfo;
        private HttpDownManager manager=HttpDownManager.getInstance();

        mDownInfo=new DownInfo();
        mDownInfo.setUrl("xxx");
        mDownInfo.setSavePath("xxx");

          manager.start(mDownInfo, new HttpDownListener() {
                    @Override
                    public void onStart() {
                        mButtonDown.setText("暂停");
                    }

                    @Override
                    public void onPause(long read) {
                        mDownInfo.setReadLength(read);
                        mButtonDown.setText("继续");
                        mTextViewProgress.setText("下载暂停");
                    }

                    @Override
                    public void onStop(long read) {
                        mDownInfo.setReadLength(read);
                        mButtonDown.setText("下载");
                        mTextViewProgress.setText("下载停止");
                    }

                    @Override
                    public void onFinish(DownInfo info) {
                        mDownInfo=info;
                        mButtonDown.setText("下载");
                        mTextViewProgress.setText("下载成功");
                    }

                    @Override
                    public void onError(DownInfo info,String s) {
                        mDownInfo=info;
                        mButtonDown.setText("下载");
                        mTextViewProgress.setText("下载失败");
                    }

                    @Override
                    public void onProgress(long currentRead, long addLength) {
                        int pro=(int)(currentRead*100/addLength);
                        mProgressBar.setProgress(pro);
                        mTextViewProgress.setText("下载中："+pro+"%");
                    }
                });

              manager.pause(mDownInfo); //暂停下载
             manager.stop(mDownInfo); //停止下载



 ## 其他
        setConnectTimeOut           设置全局连接超时时间(秒)
        setReadTimeOut              设置全局读取超时时间(秒)
        setWriteTimeOut             设置全局写入超时时间(秒)

        DownInfo    下载信息类：
            readLength      //下载进度
            countLength     //下载总长度
            url             //下载url
            savePath        //下载保持地址
            listener        //下载监听器
            state           //下载状态  1：下载中，2:暂停，3：停止，4:完成，5：错误

         DownState  下载状态类