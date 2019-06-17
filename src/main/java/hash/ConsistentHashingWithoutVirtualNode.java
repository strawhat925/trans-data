package hash;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 不带虚拟节点的一致性hash算法.
 *
 * @author zli
 * @create 2018-10-12 16:16
 **/
public class ConsistentHashingWithoutVirtualNode {

    /**
     * 待加入hash环的服务器列表
     */
    private static final String[] servers = {"192.168.0.0:1111", "192.168.0.1:2222", "192.168.0.2:3333", "192.168.0.3:4444", "192.168.0.4:5555"};

    /**
     * key表示服务器的hash值，value表示服务器的地址
     */
    private static final SortedMap<Integer, String> sortedMap = new TreeMap<>();


    static {
        Arrays.asList(servers).forEach(t -> {
            int hash = getHash(t);
            System.out.println("[" + t + "]加入集合，其hash值为" + hash);
            sortedMap.put(hash, t);
        });
        System.out.println(sortedMap);
    }


    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
     */
    private static int getHash(String address) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < address.length(); i++)
            hash = (hash ^ address.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }


    /**
     * 得到应当路由到的节点
     */
    private static String getServer(String node) {
        //得到带路由的节点hash值
        int hash = getHash(node);

        //得到大于该hash值的所有Map
        SortedMap<Integer, String> subMap = sortedMap.tailMap(hash);

        //第一个人key就是顺时针过去离node最近的那个节点
        Integer index = subMap.firstKey();
        return subMap.get(index);
    }


    public static void main(String[] args) {
        String[] nodes = {"127.0.0.1:1111", "221.226.0.1:2222", "10.211.0.1:3333"};
        Arrays.asList(nodes).forEach(t -> {
            System.out.println("[" + t + "]的hash值为" + getHash(t) + ", 被路由到节点[" + getServer(t) + "]");
        });
    }
}
