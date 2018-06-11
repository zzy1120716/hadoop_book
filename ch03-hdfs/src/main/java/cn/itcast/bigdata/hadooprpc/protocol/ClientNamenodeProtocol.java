package cn.itcast.bigdata.hadooprpc.protocol;

public interface ClientNamenodeProtocol {

    // 版本号
    public static final long versionID = 1L;

    public String getMetaData(String path);

}
