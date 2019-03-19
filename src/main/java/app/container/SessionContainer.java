package app.container;

import app.model.Request;
import app.model.Session;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.*;

import java.util.NoSuchElementException;

public class SessionContainer {
    private static SessionContainer instance = null;

    private final ObservableList<Session>     sessions = FXCollections.observableArrayList();
    private final ListChangeListener<Session> listAddChangeListener;
    private final ChangeListener<Boolean>     sessionActiveChangeListener;

    private SessionContainer() {
        listAddChangeListener = new WeakListChangeListener<>(this::handleListAddChangeEvent_AddSessionListener);
        sessionActiveChangeListener = new WeakChangeListener<>(this::handleSessionActiveChangeEvent_SessionRemoval);
    }

    private static SessionContainer initSessionContainer() {
        SessionContainer sessionContainer = new SessionContainer();
        sessionContainer.addListChangeListener(sessionContainer.listAddChangeListener);
        return sessionContainer;
    }

    public static SessionContainer getInstance() {
        if (instance == null) {
            instance = initSessionContainer();
        }
        return instance;
    }

    public ChangeListener<Boolean> getSessionActiveChangeListener() {
        return sessionActiveChangeListener;
    }

    public ObservableList<Session> getSessions() {
        return FXCollections.unmodifiableObservableList(sessions);
    }

    public Session getSession(int BannerID) throws NoSuchElementException {
        return sessions.stream()
                       .filter(session -> session.getBanner() == BannerID)
                       .findFirst()
                       .orElseThrow(NoSuchElementException::new);
    }

    void addListChangeListener(ListChangeListener<Session> listener) {
        sessions.addListener(listener);
    }

    public void checkInSession(Session session) {
        // set session to 'inactive'
        // * this should trigger a listener event for
        // * a session list removal event, which has session container remove the session from the sessions list
        // * * an availability change event, which sets the session's station and equipment availability property to true
        // * * * the availability change should trigger a notice creation event, which makes a notice if there is a waitlisted request for the newly available station/equipment
        // * * a logging/reporting event, which has a gateway or reporting class log statistical info to the database

        // request the model to remove all listeners hooked to it. (i.e. call it's 'unloadResources' method)
    }

    public void refreshSessionTimer(Session session) {
        // ...
    }

    void startSession(Request request) {
        // create session object with the request
        // * the session object should call it's init factory method
        // * * init factory method lets constructor finish in case of constructor failure and then
        // * * get station and equipable objects from request and ask the respective containers to make them unavailable

        // apply listener to session's active property that calls the handleActiveSessionChangeEvent method
        // * this method would call the removeSession method

        // add session to sessions list
        // * this should have other containers that is watching the sessions list to apply their listeners to the newly added session
        // * * listeners would be the ones mentioned in checkInSession
        // * * additionally, notice container should apply a listener for the session's timer property on reaching zero, create a notice about the person needing to be checked in or refreshed (if no one is waiting for them)
    }

    private <S extends Session> void handleListAddChangeEvent_AddSessionListener(ListChangeListener.Change<S> change) {
        change.getAddedSubList().forEach(session -> session.activeProperty().addListener(sessionActiveChangeListener));
    }

    private <B extends Boolean> void handleSessionActiveChangeEvent_SessionRemoval(ObservableValue<B> observable,
                                                                                   Boolean wasActive,
                                                                                   Boolean isActive) {
        // cast expected supertype ObservableValue<Boolean> to subtype BooleanProperty. ((this is fragile to type changes, but oh well))
        // todo: since this is rather fragile, consider having it simply remove all sessions who's active property is false.
        ReadOnlyProperty<B> activeProperty = (ReadOnlyProperty<B>) observable;
        Session             session        = (Session) activeProperty.getBean();
        if (!isActive) {
            sessions.remove(session);
        }
    }
}
