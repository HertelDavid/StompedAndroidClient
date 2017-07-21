# Stomped
An **Android** Stomp WebSocket client built using OkHttp WebSockets.

## Getting Started

### Adding the Library to your list of dependencies.

**Note:** Adding this dependency will also add OkHttp to your project.

In your Project build.gradle file add this line in the appropriate spot.

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Next, in your app build.gradle file under dependencies, add this.

```
compile 'com.github.HertelDavid:StompedAndroidClient:1.0'
```

### Use

Here is a simple example of what can be done in response to a button being pressed.

The backend Spring WebSocket server code.

```
@Controller
public class QueueController extends AbstractQueueController {

    @Autowired
    public QueueController(PersistentQueueService queueService, PersistentQueueThreadService queueThreadService) {
        super(queueService, queueThreadService);
    }

    @MessageMapping("/add/persistent-queue")
    @SendTo("/private/topic/persistent-queue")
    public String addUserToQueue(Principal principal, SimpMessageHeaderAccessor headerAccessor, UserImpl user){

        //Adds the designated user to the queue with the principal.
        //This is for later use when the queue needs to send a message back to the user.
        user.setUserPrincipal(principal);
        user.setMessagingTemplate(simpMessagingTemplate);
        persistentQueue.enqueue(user);

        notifyPersistentThread();

        return "Hello " + user.getUsername();
    }
```

And the Client code.

```
public void testClient(View view){

        final StompedClient client = new StompedClient.StompedClientBuilder().build("ws://10.0.2.2:8080/persistent-queue");
        final EditText editText = (EditText) findViewById(R.id.client_send_test);
        final TextView textView = (TextView) findViewById(R.id.client_return_test);

        client.subscribe("/private/topic/persistent-queue", new StompedListener() {

            @Override
            public void onNotify(final StompedFrame frame) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(frame.getStompedBody());
                        client.disconnect();
                    }
                });
            }
        });

        client.send("/app/add/persistent-queue", "{\"username\":\"" + editText.getText().toString() + "\"}");
    }
```

### Explanation

In this example, a button was pressed and a new connection to the Spring Stomp server was created. In the backend code there is a mapping to the server (\app\add\persistent-queue) and a mapping back to the client (/private/topic/persistent-queue). The client subscribes to the path that sends messages from the server back to the client and then sends a message to the server with the associated payload which is of the JSON MIME type.

You can either create an anonymous StompedListener object or implement the StompedListener class in another file. Only the listener object passed to the subscribe method will be called when a server sends a message back to the client.

**Note:** The parameter type in the onNotify method contains all information related to the message sent back
to the client. This includes details to the Command sent back, the headers, and, most importantly, the body.

### More Details

Connecting to the Server example

```
final StompedClient stompedClient = new StompedClient.StompedClientBuilder()
	.build("ws://10.0.2.2:8080/persistent-queue");
```

Subscribing to a path

```
stompedClient.subscribe("/your/path", new StompedListener(){

	@Override
	public void onNotify(final StompedFrame frame){
		//Do work here.
	}
})
```

Retrieving Your Data

```
frame.getStompedBody();
// Or
frame.getStompedHeaders();
frame.getHeaderValueFromKey(StompedHeaders.STOMP_HEADER_DESTINATION);
```

Sending a Message with a Payload (You can always send a message with no payload too)

```
stompedClient.send("/your/path/here", "{\"firstName\":\"David\"}");
```

You can either create an anonymous StompedListener object or implement the StompedListener class in another file. Only the listener object passed to the subscribe method will be called when a server sends a message back to the client.

### Some Important Points

* Always make sure to add this to your Manifest.

```
<uses-permission android:name="android.permission.INTERNET" />
```

* If you expect a message back from some method on the server in response to sending a message to the server, then always subscribe to the desired path before sending the message to the server. This will get rid of issues of subscribing to a path after the server has already sent a message back.

* In my opinion, it is good practice to disconnect from the server if no data is being passed to the server or back to the client or if there are no future messages to be sent or received.


