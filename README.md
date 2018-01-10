# RRODemo

    对retrofit2+rxjava2+okhttp3 进行封装使用，尽可能方便的调用
    封装功能：普通请求（post,get）
              文件上传（文件和信息可同时，提供文件封装方法，达到和普通调用一样）
              文件下载（可多文件同时，带进度）


##普通请求调用

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


##文件上传

