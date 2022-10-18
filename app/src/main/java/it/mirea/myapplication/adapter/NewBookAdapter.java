package it.mirea.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.mirea.myapplication.BookActivity;
import it.mirea.myapplication.R;
import it.mirea.myapplication.ViewUsersBookActivity;
import it.mirea.myapplication.model.Book;
import it.mirea.myapplication.model.NewBook;

public class NewBookAdapter extends RecyclerView.Adapter<NewBookAdapter.NewBookViewHolder>{
    Context context;
    List<NewBook> books;

    public NewBookAdapter(Context context, List<NewBook> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public NewBookAdapter.NewBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View bookItems = LayoutInflater.from(context).inflate(R.layout.database_book_item, parent, false);
        return new NewBookAdapter.NewBookViewHolder(bookItems);
    }

    @Override
    public void onBindViewHolder(@NonNull NewBookAdapter.NewBookViewHolder holder, int position) {
        //int imageId = context.getResources().getIdentifier("ic_" + books.get(position).getImg(), "drawable", context.getPackageName());
        //holder.bookImage.setImageResource(imageId);

        holder.bookFolderName.setText(books.get(position).getTitle());
        //holder.bookType.setText(books.get(position).getType());
        //holder.pageNumber.setText(books.get(position).getNumber_of_pages());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewUsersBookActivity.class);

                //intent.putExtra("bookImage", imageId);
                intent.putExtra("bookTitle", books.get(position).getTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static final class NewBookViewHolder extends RecyclerView.ViewHolder {

        ImageView bookImage;
        TextView bookFolderName;

        public NewBookViewHolder(@NonNull View itemView) {
            super(itemView);


            bookFolderName = itemView.findViewById(R.id.bookFolderName);
        }
    }
}
