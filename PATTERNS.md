# patterns gists

## Watcher vs. Container

A watcher and a container are both used to help manage models, though their purpose and structure are very different

### Watcher
A watcher's goal is to act as a list that contains readable metadata about it's contents, 
  where the metadata is expected to be used as an entry for a tableview or listview.
  when any of the values belonging to an object that the has watcher listed changes, then
  the watcher will update it's own properties to reflect the new information to be represented
  by the watcher. a watcher is meant to represent a single row/entry of info where that info 
  depends on the current values within the items in it's list.
  
  example: a StationNameWatcher is a watcher that has a list of stations of the same name. the 
  watcher displays properties about how many stations it has listed and how many stations are
  considered available. if a station in the watcher's list changes it's availability value then
  the watcher will update it's own properties to reflect the new information so that it's always
  displaying the correct info automatically. 

  when to use? use when you want to represent information about a collection of items and the 
  represented information depends on the state of each item itself.
  
### Container
A container's goal is to manage the lifecycle of a type of object and may act as a collection of all 
  instances for that given object. 
  it's duty is to create, hold, handle, and delete objects according to business logic. 
  It may organize the collection of objects in ways that are useful for other objects to utilize. 
  The primary reason for a container is to abstract object creation away from controllers, 
  allowing a uniform way to handle an object. 
  It is essentially an API for the given object so that the object's class isn't cluttered. 
  
  example: a RequestContainer is a container that can create a request when given the required
  arguments, handle a request by supplying a "attemptRequestCheckOut" method that would take the request and
  either give it to a SessionContainer so it can be handles in a session object, or it would take the request and
  give it to a WaitlistContainer so it can be handled in a waitlist object. The controller that called the 
  method doesn't need to know how it did it, it just needs it done. Finally, the request container may 
  allow a controller to ask for a given request or list of requests so it can be show to a table view.
  
  when to use? use when you need an object to be fully handled in a uniform manner within a centralized
  class. 
