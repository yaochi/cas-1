package org.apereo.cas.ticket.registry.dao;

import org.apereo.cas.ticket.Tickets;
import org.apereo.cas.ticket.registry.serializer.JacksonStringTicketSerializer;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.TicketGrantingTicketImpl;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * @author David Rodriguez
 *
 * @since 5.1.0
 */
public class DefaultCassandraTicketRegistryDaoJacksonStringSerializationTests {

    @Rule
    public CassandraCQLUnit cassandraUnit = new CassandraCQLUnit(new ClassPathCQLDataSet("schema.cql"), 
            "cassandra.yaml", 120_000L);
    private DefaultCassandraTicketRegistryDao<String> dao;

    @Before
    public void setUp() throws Exception {
        dao = new DefaultCassandraTicketRegistryDao<>("localhost", 
                "", "", 
                new JacksonStringTicketSerializer(), 
                String.class, 
                "cas.ticketgrantingticket",
                "cas.serviceticket", 
                "cas.ticket_cleaner", 
                "cas.ticket_cleaner_lastrun");
    }

    @Test
    public void shouldWorkWithAStringSerializer() throws Exception {
        final TicketGrantingTicketImpl tgt = Tickets.createTicketGrantingTicket("id");
        dao.addTicketGrantingTicket(tgt);
        assertEquals(tgt, dao.getTicketGrantingTicket("id"));
    }

    @Ignore("To be completed")
    @Test
    public void shouldReturnExpiredTGTs() throws Exception {
        // given
        final TicketGrantingTicket firstExpired = Tickets.createExpiredTicketGrantingTicket("expired1");
        final TicketGrantingTicket secondExpired = Tickets.createExpiredTicketGrantingTicket("expired2");
        final TicketGrantingTicket notExpired = Tickets.createTicketGrantingTicket("notExpired");

        dao.addTicketGrantingTicket(firstExpired);
        dao.addTicketGrantingTicket(secondExpired);
        dao.addTicketGrantingTicket(notExpired);

        // when
        final Stream<TicketGrantingTicket> expiredTgts = dao.getExpiredTicketGrantingTickets();

        // then
        final long expiredTgtsInserted = 2;
        assertThat(expiredTgts.count(), is(expiredTgtsInserted));
    }
}
