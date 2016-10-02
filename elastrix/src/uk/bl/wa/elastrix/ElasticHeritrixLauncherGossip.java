package uk.bl.wa.elastrix;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.code.gossip.GossipMember;
import com.google.code.gossip.GossipService;
import com.google.code.gossip.GossipSettings;
import com.google.code.gossip.LocalGossipMember;
import com.google.code.gossip.RemoteGossipMember;
import com.google.code.gossip.event.GossipListener;
import com.google.code.gossip.event.GossipState;
import com.google.common.hash.Hashing;

/**
 * 
 * Example of using the Gossip protocol to discover cluster members
 * 
 */
public class ElasticHeritrixLauncherGossip {

    private static final Logger log = Logger
            .getLogger(ElasticHeritrixLauncherGossip.class);

    public static void main(String[] args) throws Exception {

        GossipSettings settings = new GossipSettings();
        int seedNodes = 3;
        List<GossipMember> startupMembers = new ArrayList<>();
        for (int i = 1; i < seedNodes+1; ++i) {
            startupMembers
                    .add(new RemoteGossipMember("127.0.0.1", 2000 + i, i + ""));
        }

        final List<GossipService> clients = new ArrayList<>();
        final int clusterMembers = 5;
        for (int i = 1; i < clusterMembers+1; ++i) {
            System.out.println("Launching " + i);
            GossipService gossipService = new GossipService("127.0.0.1",
                    2000 + i, i + "", 
                    startupMembers, settings,
                    new GossipListener(){
                @Override
                public void gossipEvent(GossipMember member, GossipState state) {
                    System.out.println(member+" "+ state);
                }
            });
            clients.add(gossipService);
            gossipService.start();
        }

        int total = 0;
        for (int i = 0; i < clusterMembers; ++i) {
            List<LocalGossipMember> list = clients.get(i).get_gossipManager()
                    .getMemberList();
            System.out.println("Size " + list.size());
            total += list.size();
            System.out.println("I+ " + (i + 1));
            for (GossipMember m : list) {
                System.out.println("M " + m);
            }
        }

        System.out.println("Total " + total);

        Thread.sleep(10000);

        total = 0;
        for (int i = 0; i < clusterMembers; ++i) {
            List<LocalGossipMember> list = clients.get(i).get_gossipManager()
                    .getMemberList();
            System.out.println("Size " + list.size());
            total += list.size();
            System.out.println("I+ " + (i + 1));
            for (GossipMember m : list) {
                System.out.println("M " + m);
            }
        }

        System.out.println("Total " + total);

        for(int i = 0;i<clusterMembers;++i)
        {
            GossipService m = clients.get(i);
            if (m != null) {
                m.shutdown();
            }
        }

        // heritrix.getEngine().getJob("sds").getBeanpathTarget("SS").setMembership;

        // https://arxiv.org/ftp/arxiv/papers/1406/1406.2294.pdf
        Hashing.consistentHash(1, 10);

        System.exit(1);

    }

}
