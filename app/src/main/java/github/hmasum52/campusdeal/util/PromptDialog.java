package github.hmasum52.campusdeal.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import github.hmasum52.campusdeal.R;

// https://www.youtube.com/watch?v=Zi6rccophco
public class PromptDialog {
    private Context context;

    private Dialog dialog;


    public PromptDialog(Context context, int layout_id) {
        this.context = context;

        dialog = new Dialog(context);
        dialog.setContentView(layout_id);
        dialog.setCancelable(false);
    }

    public void showDialog(String message, int view_id){
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView textView = dialog.findViewById(view_id);
        if(textView!=null)
            textView.setText(message);
        dialog.show();
    }

    public Button getYestButton(){
        return dialog.findViewById(R.id.yes_btn);
    }

    public Button getNoButton(){
        return dialog.findViewById(R.id.no_btn);
    }

    public void showDialog(){
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public void hideDialog(){
        dialog.dismiss();
    }

    public <T extends View> T findViewById(int id){
        return dialog.findViewById(id);
    }
}
