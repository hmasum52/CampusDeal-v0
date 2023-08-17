package github.hmasum52.campusdeal.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

// https://www.youtube.com/watch?v=Zi6rccophco
public class LoadingDialogBar {
    private Context context;

    private Dialog dialog;

    private int layout_id;

    public LoadingDialogBar(Context context, int layout_id) {
        this.context = context;
        this.layout_id = layout_id;
    }

    public void showDialog(String message, int view_id){
        dialog = new Dialog(context);
        dialog.setContentView(layout_id);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView textView = dialog.findViewById(view_id);
        if(textView!=null)
            textView.setText(message);
        dialog.show();
    }

    public void hideDialog(){
        dialog.dismiss();
    }
}
