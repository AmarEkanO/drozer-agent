package com.mwr.droidhg.agent;

import java.util.Observable;
import java.util.Observer;

import com.mwr.droidhg.Agent;
import com.mwr.droidhg.agent.views.ConnectorStatusIndicator;
import com.mwr.droidhg.agent.views.ServerParametersDialog;
import com.mwr.droidhg.api.ServerParameters;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class ServerActivity extends Activity implements Observer {

	private ServerParameters parameters = null;
	private CheckBox server_enabled = null;
	private ConnectorStatusIndicator server_status_indicator = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.activity_server);
        
        this.server_enabled = (CheckBox)this.findViewById(R.id.server_enabled);
        this.server_status_indicator = (ConnectorStatusIndicator)this.findViewById(R.id.server_status_indicator);
        
        this.setServerParameters(Agent.getServerParameters());
        
        this.server_enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					Agent.startServer();
				else
					Agent.stopServer();
			}
        	
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.activity_server, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
//    	case R.id.menu_delete_endpoint:
//    		new AlertDialog.Builder(this)
//    			.setIcon(android.R.drawable.ic_dialog_alert)
//    			.setTitle(R.string.delete_endpoint)
//    			.setMessage(R.string.confirm_delete_endpoint)
//    			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//
//    				@Override
//    				public void onClick(DialogInterface dialog, int which) {
//    					Agent.stopEndpoint(endpoint);
//    					
//    					if(Agent.getEndpointManager().remove(EndpointActivity.this.endpoint)) {
//    						Toast.makeText(EndpointActivity.this.getApplicationContext(), "Removed Endpoint", Toast.LENGTH_SHORT).show();
//    						
//    						EndpointActivity.this.finish();
//    					}
//    					else {
//    						Toast.makeText(EndpointActivity.this.getApplicationContext(), "Error removing Endpoint", Toast.LENGTH_SHORT).show();
//    					}
//    				}
//    				
//    			})
//    			.setNegativeButton(R.string.no, null)
//    			.show();
//    		return true;
//    		
    	case R.id.menu_edit_server:
    		Agent.stopServer();
			
    		ServerParametersDialog dialog = new ServerParametersDialog(this);
    		dialog.setParameters(this.parameters);
        	dialog.setOnSaveListener(new ServerParametersDialog.OnSaveListener() {
				
				@Override
				public boolean onSave(ServerParameters parameters) {
					if(ServerActivity.this.parameters.update(parameters)) {
						Toast.makeText(ServerActivity.this.getApplicationContext(), "Updated Server", Toast.LENGTH_SHORT).show();
						
						return true;
					}
					else {
						Toast.makeText(ServerActivity.this.getApplicationContext(), "Error updating Server", Toast.LENGTH_SHORT).show();
						
						return false;
					}
				}
				
			});
        	dialog.create().show();
    		return true;
    	
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	Agent.unbindServices();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	Agent.bindServices();
    }
    
    private void setServerParameters(ServerParameters parameters) {
    	if(this.parameters != null)
    		this.parameters.deleteObserver(this);
    	
    	this.parameters = parameters;
    	
    	this.server_enabled.setChecked(this.parameters.isEnabled());
    	this.server_status_indicator.setConnector(this.parameters);
    	
    	this.parameters.addObserver(this);
    }

	@Override
	public void update(Observable observable, Object data) {
		this.setServerParameters((ServerParameters)observable);
	}

}