package com.termux.app.terminal;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.termux.R;
import com.termux.app.TermuxService;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalSession;

import java.util.List;

/**
 * Adapter for the terminal session list in the drawer.
 * Adapted from AndroidIDE's TermuxSessionsListViewController.
 */
public class TermuxSessionsListViewController extends ArrayAdapter<TermuxSession>
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private final com.neo.ide.app.BaseActivity mActivity;
    private final TermuxTerminalSessionActivityClient mSessionClient;

    final StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
    final StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);

    public TermuxSessionsListViewController(
            com.neo.ide.app.BaseActivity activity,
            TermuxTerminalSessionActivityClient sessionClient,
            List<TermuxSession> sessionList
    ) {
        super(activity.getApplicationContext(), R.layout.item_terminal_session, sessionList);
        this.mActivity = activity;
        this.mSessionClient = sessionClient;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View sessionRowView = convertView;
        if (sessionRowView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            sessionRowView = inflater.inflate(R.layout.item_terminal_session, parent, false);
        }

        TextView sessionTitleView = sessionRowView.findViewById(R.id.session_title);

        TermuxSession termuxSession = getItem(position);
        if (termuxSession == null) {
            sessionTitleView.setText("null session");
            return sessionRowView;
        }

        TerminalSession sessionAtRow = termuxSession.getTerminalSession();
        if (sessionAtRow == null) {
            sessionTitleView.setText("null session");
            return sessionRowView;
        }

        String name = sessionAtRow.mSessionName;
        String sessionTitle = sessionAtRow.getTitle();

        String numberPart = "[" + (position + 1) + "] ";
        String sessionNamePart = TextUtils.isEmpty(name) ? "" : name;
        String sessionTitlePart = TextUtils.isEmpty(sessionTitle) ? ""
            : ((sessionNamePart.isEmpty() ? "" : "\n") + sessionTitle);

        String fullSessionTitle = numberPart + sessionNamePart + sessionTitlePart;
        SpannableString fullSessionTitleStyled = new SpannableString(fullSessionTitle);
        fullSessionTitleStyled.setSpan(boldSpan, 0, numberPart.length() + sessionNamePart.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fullSessionTitleStyled.setSpan(italicSpan, numberPart.length() + sessionNamePart.length(), fullSessionTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sessionTitleView.setText(fullSessionTitleStyled);

        boolean sessionRunning = sessionAtRow.isRunning();
        if (sessionRunning) {
            sessionTitleView.setPaintFlags(sessionTitleView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            sessionTitleView.setPaintFlags(sessionTitleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        int color = sessionRunning || sessionAtRow.getExitStatus() == 0 ? Color.WHITE : Color.RED;
        sessionTitleView.setTextColor(color);

        return sessionRowView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TermuxSession clickedSession = getItem(position);
        if (clickedSession != null) {
            mSessionClient.setCurrentSession(clickedSession.getTerminalSession());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // Long press: no-op for now (could add rename dialog)
        return true;
    }
}
