package mylocation.example.logandreg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    List<User> data;
    TextView username;
    TextView type;

    public UserAdapter(List<User> data, Context context)
    {
        this.context=context;
        this.data=data;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User geter1 =  data.get(position);
        String login, tipas;
        login=geter1.getLogin();
        tipas=geter1.getType();

        username.setText(login);
        type.setText(tipas);



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);

            type=(TextView)itemView.findViewById(R.id.type);
            username=(TextView)itemView.findViewById(R.id.username);


        }
    }
}

