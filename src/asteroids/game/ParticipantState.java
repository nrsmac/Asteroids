package asteroids.game;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Keeps track of the Participants, their motions, and their collisions.
 */
public class ParticipantState implements Iterable<Participant>
{
    /** The participants (asteroids, ships, etc.) that are involved in the game */
    private LinkedList<Participant> participants;

    /** Participants that are waiting to be added to the game */
    private Set<Participant> pendingAdds;

    /**
     * Creates an empty ParticipantState.
     */
    public ParticipantState ()
    {
        // No participants at the start
        participants = new LinkedList<Participant>();
        pendingAdds = new HashSet<Participant>();
    }

    /**
     * Clears out the state.
     */
    public void clear ()
    {
        pendingAdds.clear();
        for (Participant p : participants)
        {
            Participant.expire(p);
        }
        participants.clear();
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pendingAdds.add(p);
    }

    /**
     * Moves each of the active participants to simulate the passage of time.
     */
    public void moveParticipants ()
    {
        // Move all of the active participants
        for (Participant p : participants)
        {
            if (!p.isExpired())
            {
                p.move();
            }
        }

        // If there have been any collisions, deal with them. This may result
        // in new participants being added or old ones expiring. We save those
        // changes until after all of the collisions have been processed.
        checkForCollisions();

        // Deal with pending adds and expirations
        completeAddsAndRemoves();
    }

    /**
     * Completes any adds and removes that have been requested.
     */
    private void completeAddsAndRemoves ()
    {
        // Note: These updates are saved up and done later to avoid modifying
        // the participants list while it is being iterated over
        for (Participant p : pendingAdds)
        {
            participants.add(p);
        }
        pendingAdds.clear();

        Iterator<Participant> iter = participants.iterator();
        while (iter.hasNext())
        {
            Participant p = iter.next();
            if (p.isExpired())
            {
                iter.remove();
            }
        }
    }

    /**
     * Compares each pair of elements to detect collisions, then notifies all listeners of any found. Deals with each
     * pair only once. Never deals with (p1,p2) and then again with (p2,p1).
     */
    private void checkForCollisions ()
    {
        for (Participant p1 : participants)
        {
            if (!p1.isExpired() && !p1.isInert())
            {
                Iterator<Participant> iter = participants.descendingIterator();
                while (iter.hasNext())
                {
                    Participant p2 = iter.next();
                    if (p1 == p2)
                        break;
                    if (!p2.isExpired() && !p2.isInert() && p1.overlaps(p2))
                    {
                        p1.collidedWith(p2);
                        p2.collidedWith(p1);
                    }
                    if (p1.isExpired())
                        break;
                }
            }
        }
    }
    
    
    /**
     * Returns an Iterator that makes it possible to iterate through all the unexpired participants via an enhanced for loop, as in
     * 
     *  <pre>
     *  for (Participant p: pstate)
     *  {
     *      ....;
     *  }</pre>
     *  
     *  assuming that pstate is a ParticipantState object.
     */
    @Override
    public Iterator<Participant> iterator ()
    {
        return new ParticipantIterator();
    }
    
    /**
     * Represents an Iterator over the unexpired participants.
     */
    private class ParticipantIterator implements Iterator<Participant>
    {
        /** An iterator over the participants */
        private Iterator<Participant> participantsIter;
        
        /** An iterator over the pendingAdds */
        private Iterator<Participant> pendingsIter;
        
        /** If non-null, the next object to be returned by the next() method */
        private Participant saved;
        
        /**
         * Creates a ParticipantIterator 
         */
        public ParticipantIterator ()
        {
            participantsIter = participants.iterator();
            pendingsIter = pendingAdds.iterator();
            saved = null;
        }
        
        @Override
        public boolean hasNext ()
        {
            if (saved != null)
            {
                return true;
            }
            
            while (participantsIter.hasNext())
            {
                Participant p = participantsIter.next();
                if (!p.isExpired())
                {
                    saved = p;
                    return true;
                }
            }
            
            while (pendingsIter.hasNext())
            {
                Participant p = pendingsIter.next();
                if (!p.isExpired())
                {
                    saved = p;
                    return true;
                }
            }
            
            return false;
        }
        
        @Override
        public Participant next ()
        {
            if (saved != null)
            {
                Participant p = saved;
                saved = null;
                return p;
            }
            
            while (participantsIter.hasNext())
            {
                Participant p = participantsIter.next();
                if (!p.isExpired())
                {
                    return p;
                }
            }
            
            while (pendingsIter.hasNext())
            {
                Participant p = pendingsIter.next();
                if (!p.isExpired())
                {
                    return p;
                }
            }
            
            throw new NoSuchElementException();
        }       
    }
}
