package uk.bl.wa.elastrix;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.atomix.AtomixReplica;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.group.DistributedGroup;
import io.atomix.group.GroupMember;
import io.atomix.group.LocalMember;
import io.atomix.group.messaging.MessageConsumer;


public class ElasticHeritrixLauncher {

    public static void main(String[] args) throws Exception {
        if (args.length < 2)
            throw new IllegalArgumentException(
                    "must supply a local port and at least one remote host:port tuple");

        int port = Integer.valueOf(args[0]);

        Address address = new Address(InetAddress.getLocalHost().getHostName(),
                port);

        List<Address> cluster = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            String[] parts = args[i].split(":");
            Address a = new Address(parts[0], Integer.valueOf(parts[1]));
            System.out.println("Address " + a);
            cluster.add(a);
        }

        AtomixReplica atomix = AtomixReplica.builder(address)
                .withTransport(new NettyTransport())
                .withStorage(Storage.builder()
                        .withDirectory(System.getProperty("user.dir") + "/logs/"
                                + UUID.randomUUID().toString())
                        .build())
                .build();

        atomix.bootstrap(cluster).get();

        System.out.println("Creating membership group");
        DistributedGroup group = atomix.getGroup("group").get();

        group.onJoin(member -> {
            System.out.println(member.id() + " joined the group!");

            member.messaging().producer("tasks").send("hello").thenRun(() -> {
                System.out.println("Task complete!");
            });
        });

        group.onLeave(member -> {
            System.out.println(member.id() + " left the group!");
        });

        group.onStateChange(state -> {
            System.out.println("State changed to " + state);
        });

        System.out.println("Joining membership group");
        CompletableFuture<LocalMember> joining = group.join("i.am."+port);
        joining.thenAccept(member -> {
            System.out.println("Joined group with member ID: " + member.id());
            MessageConsumer<String> consumer = member.messaging()
                    .consumer("tasks");
            consumer.onMessage(task -> {
                System.out.println("Received message");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    task.ack();
                }
            });
        });
        LocalMember localMember = joining.get();

        for (int i = 0; i < Integer.parseInt(args[1]); i++) {
            Thread.sleep(1000);
            for (GroupMember g2m : group.members()) {
                System.out.println("g " + g2m);
            }
            System.out.println("GS " + group.state() + " " + group.isOpen());
        }

        localMember.leave();

        System.exit(0);
    }

}
