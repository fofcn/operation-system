//package com.github.futurefs.store.distributed;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.github.futurefs.store.rpc.config.RpcConfig;
//import lombok.Data;
//
//import java.util.List;
//
///**
// * 集群配置类
// *
// * @author errorfatal89@gmail.com
// * @datetime 2022/04/01 16:57
// */
//@Data
//public class ClusterConfig {
//
//    /**
//     * 集群模式
//     */
//    @JsonProperty
//    private int mode;
//
//    /**
//     * 集群角色
//     */
//    @JsonProperty
//    private int role;
//
//    /**
//     * 对等端ID
//     */
//    @JsonProperty
//    private int peerId;
//
//    /**
//     * Rpc配置
//     */
//    @JsonProperty
//    private RpcConfig rpcConfig;
//
//    /**
//     *
//     */
//    @JsonProperty
//    private List<String> peerList;
//}
