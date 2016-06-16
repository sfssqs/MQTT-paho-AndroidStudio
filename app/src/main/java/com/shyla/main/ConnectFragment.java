package com.shyla.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shyla.asmqtt.MessageListener;
import com.shyla.asmqtt.R;
import com.shyla.asmqtt.RemoteControl;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConnectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ConnectFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RemoteControl mRemoteControl;

    public ConnectFragment() {
        // Required empty public constructor
    }

    private MessageListener mMessageListener = new MessageListener() {
        @Override
        public void onMessageArrived(final String message) {
            Log.v(TAG, "onMessageArrived, message : " + message);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) getView().findViewById(R.id.text_receive_info)).setText("Received info : \n" + message);
                }
            });
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConnectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConnectFragment newInstance(String param1, String param2) {
        ConnectFragment fragment = new ConnectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_lock_door).setOnClickListener(this);
        view.findViewById(R.id.btn_unlock_door).setOnClickListener(this);
        view.findViewById(R.id.btn_open_trunk).setOnClickListener(this);

        view.findViewById(R.id.btn_connect).setOnClickListener(this);
        view.findViewById(R.id.btn_subscribe).setOnClickListener(this);
        view.findViewById(R.id.btn_publish).setOnClickListener(this);
        view.findViewById(R.id.btn_disconnect).setOnClickListener(this);
        view.findViewById(R.id.btn_ssl).setOnClickListener(this);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mRemoteControl = RemoteControl.getInstance();
        mRemoteControl.addListener(mMessageListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mRemoteControl.removeListener(mMessageListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lock_door:
                mRemoteControl.publish("lock_door", "control");
                break;
            case R.id.btn_unlock_door:
                mRemoteControl.publish("unlock_door", "control");
                break;
            case R.id.btn_open_trunk:
                mRemoteControl.publish("open_trunk", "control");
                break;

            case R.id.btn_connect:
                mRemoteControl.connect(false);
                break;
            case R.id.btn_subscribe:
                mRemoteControl.subscribe();
                break;
            case R.id.btn_publish:
                mRemoteControl.publish("test message", "control");
                break;
            case R.id.btn_disconnect:
                mRemoteControl.disconnect();
                break;
            case R.id.btn_ssl:
                mRemoteControl.connect(true);
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
