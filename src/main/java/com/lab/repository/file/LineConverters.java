package com.lab.repository.file;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;

import java.time.LocalDateTime;

/**
 * Conversions between entities and the text lines used in the data files.
 * The methods are meant to be passed to {@link FileRepository} as method references.
 */
public class LineConverters {
    private LineConverters(){

    }

    /**
     * Parses a user from a line with the format {@code id;firstName;lastName}
     * @param line the text line
     * @return the parsed user
     * @throws RepositoryException if the line does not have exactly 3 fields
     * @throws NumberFormatException if the id is not a number
     */
    public static User parseUser(String line){
        String[] attributes = line.split(";" ,-1);
        if(attributes.length != 3)
            throw new RepositoryException("The line must contain 3 attributes delimited by ';'");
        User user = new User();
        user.setId(Long.parseLong(attributes[0].trim()));
        user.setFirstName(attributes[1].trim());
        user.setLastName(attributes[2].trim());
        return user;
    }

    /**
     * Formats a user as {@code id;firstName;lastName}.
     * @param user the user to format
     * @return the text line
     */
    public static String formatUser(User user){
        return user.getId() + ";" + user.getFirstName() + ";" + user.getLastName();
    }

    /**
     * Parses a friendship from a line with the format {@code id1;id2;date}.
     * The ids are normalized so that the smaller one is always on the left.
     * @param line the text line
     * @return the parsed friendship
     * @throws RepositoryException if the line does not have exactly 3 fields
     * @throws NumberFormatException if an id is not a number
     * @throws java.time.format.DateTimeParseException if the date is invalid
     */
    public static Friendship parseFriendship(String line){
        String[] attributes = line.split(";" , -1);
        if(attributes.length != 3)
            throw new RepositoryException("The line must contain 3 attributes delimited by ';' ");
        Friendship friendship = new Friendship();

        Long left = Long.parseLong(attributes[0].trim()) ;
        Long right = Long.parseLong(attributes[1].trim());
        
        friendship.setId(new Tuple<>(Math.min(left, right), Math.max(left, right)));
        friendship.setDate(LocalDateTime.parse(attributes[2].trim()));
        return friendship;
    }

    /**
     * Formats a friendship as {@code id1;id2;date}.
     * @param friendship the friendship to format
     * @return the text line
     */
    public static String formatFriendship(Friendship friendship){
        return friendship.getId().getLeft() + ";" + friendship.getId().getRight() + ";" + friendship.getDate();
    }
}
