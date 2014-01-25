package com.kronosad.projects.kronoskonverse.client.gui;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.style.INotificationStyle;
import ch.swingfx.twinkle.style.theme.DarkDefaultNotification;
import ch.swingfx.twinkle.window.Positions;
import com.kronosad.projects.kronoskonverse.common.packets.Packet02ChatMessage;
import com.kronosad.projects.kronoskonverse.common.packets.Packet03UserListChange;

/**
 * User: russjr08
 * Date: 1/19/14
 * Time: 1:16 PM
 */
public class NotificationHelper {
    private static INotificationStyle style = new DarkDefaultNotification().withAlpha(0.9f).withWidth(400);

    @Deprecated
    public static void userLoggedIn(Packet03UserListChange packet){
        new NotificationBuilder().withDisplayTime(10000).
                withStyle(style)
                .withPosition(Positions.NORTH_EAST)
                .withTitle("KronosKonverse" +
                        " - User Logged In!")
//                .withMessage(String.format("%s has just logged in!", packet.getUser().getUsername()))
                .showNotification();
    }

    public static void mentioned(Packet02ChatMessage packet){
        String title;
        if(packet.isPrivate()){
            title = "KronosKonverse - New Private Message!";
        }else{
            title = "KronosKonverse - You were mentioned!";
        }
        new NotificationBuilder().withDisplayTime(10000).
                withStyle(style)
                .withPosition(Positions.NORTH_EAST)
                .withTitle(title)
                .withMessage(String.format("%s mentioned your name!", packet.getChat().getUser().getUsername()))
                .showNotification();
    }

}
