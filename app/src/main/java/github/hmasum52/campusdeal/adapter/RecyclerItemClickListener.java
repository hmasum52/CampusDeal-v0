package github.hmasum52.campusdeal.adapter;

public class RecyclerItemClickListener<T>{
    public interface OnItemClickListener<T>{
        void onItemClick(T item);
    }
}
