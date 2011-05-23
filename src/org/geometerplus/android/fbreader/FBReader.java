/*
 * Copyright (C) 2009-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import org.geometerplus.zlibrary.text.hyphenation.ZLTextHyphenator;

import com.sean.android.ebookmain.R;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;

import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.library.Book;

import org.geometerplus.android.fbreader.library.KillerCallback;

import org.geometerplus.android.util.UIUtil;
import android.util.Log;
//sean_0517
import java.io.FileInputStream;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.view.Window;
//import com.sean.android.ebookmain.CoverFlow;

//sean_0517
public final class FBReader extends ZLAndroidActivity implements View.OnClickListener, ActionBar.TabListener     {
	public static final String BOOK_PATH_KEY = "BookPath";
        private static final String TAG = "FBReader";
	final static int REPAINT_CODE = 1;
	final static int CANCEL_CODE = 2;

	private int myFullScreenFlag;

	private static TextSearchButtonPanel ourTextSearchPanel;
	private static NavigationButtonPanel ourNavigatePanel;

//sean_0517
    private View mCustomView;
    private Button mlocal;
    private Button mnetwork;
    private Button mbookmark;
    private Button msetting;    

    private FBReaderApp fbReader ;

	@Override
	protected ZLFile fileFromIntent(Intent intent) {
		String filePath = intent.getStringExtra(BOOK_PATH_KEY);
		if (filePath == null) {
			final Uri data = intent.getData();
			if (data != null) {
				filePath = data.getPath();
			}
		}
		return filePath != null ? ZLFile.createFileByPath(filePath) : null;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
              Log.v(TAG, "SEAN_LOG  onCreate " ); 
		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		myFullScreenFlag =
			application.ShowStatusBarOption.getValue() ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
		  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, myFullScreenFlag);

		//sean_0517final FBReaderApp fbReader = (FBReaderApp)FBReaderApp.Instance();
		fbReader = (FBReaderApp)FBReaderApp.Instance();
		if (ourTextSearchPanel == null) {
			ourTextSearchPanel = new TextSearchButtonPanel(fbReader);
		}
		if (ourNavigatePanel == null) {
			ourNavigatePanel = new NavigationButtonPanel(fbReader);
		}

		fbReader.addAction(ActionCode.SHOW_LIBRARY, new ShowLibraryAction(this, fbReader));
		fbReader.addAction(ActionCode.SHOW_PREFERENCES, new ShowPreferencesAction(this, fbReader));
		fbReader.addAction(ActionCode.SHOW_BOOK_INFO, new ShowBookInfoAction(this, fbReader));
		fbReader.addAction(ActionCode.SHOW_TOC, new ShowTOCAction(this, fbReader));
		fbReader.addAction(ActionCode.SHOW_BOOKMARKS, new ShowBookmarksAction(this, fbReader));
		fbReader.addAction(ActionCode.SHOW_NETWORK_LIBRARY, new ShowNetworkLibraryAction(this, fbReader));
		
		fbReader.addAction(ActionCode.SHOW_MENU, new ShowMenuAction(this, fbReader));
		fbReader.addAction(ActionCode.SHOW_NAVIGATION, new ShowNavigationAction(this, fbReader));
		fbReader.addAction(ActionCode.SEARCH, new SearchAction(this, fbReader));

		fbReader.addAction(ActionCode.PROCESS_HYPERLINK, new ProcessHyperlinkAction(this, fbReader));

		fbReader.addAction(ActionCode.SHOW_CANCEL_MENU, new ShowCancelMenuAction(this, fbReader));
        
 //sean_0517
/*
        CoverFlow coverFlow;
        coverFlow =  (CoverFlow) findViewById(R.id.coverflow);
        ImageAdapter coverImageAdapter =  new ImageAdapter(this);
        
        coverImageAdapter.createReflectedImages();
        
        coverFlow.setAdapter(coverImageAdapter);
        
        coverFlow.setSpacing(-15);
        coverFlow.setSelection(5, true);
 */       
/*        
        findViewById(R.id.local_library).setOnClickListener(this);
        findViewById(R.id.network_library).setOnClickListener(this);
        findViewById(R.id.bookmark).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
*/
        //initial button
        initializeButton();
        //show  button
        setAllButtonVisible();
        //hide home title 
        setHomeVisible();

 
        mCustomView = getLayoutInflater().inflate(R.layout.action_bar_display_options_custom, null);
        // Configure several action bar elements that will be toggled by display options.
        final ActionBar bar = getActionBar();
        bar.setCustomView(mCustomView,
                new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        bar.addTab(bar.newTab().setText("Tab 1").setTabListener(this));
        bar.addTab(bar.newTab().setText("Tab 2").setTabListener(this));
        bar.addTab(bar.newTab().setText("Tab 3").setTabListener(this));
        

 //sean_0517

        
	}

 	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	         Log.v(TAG, "SEAN_LOG  onPrepareOptionsMenu " ); 
		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		if (!application.ShowStatusBarOption.getValue() &&
			application.ShowStatusBarWhenMenuIsActiveOption.getValue()) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
    public void invalidateOptionsMenu() {
        // TODO Auto-generated method stub
        Log.v(TAG, "SEAN_LOG  invalidateOptionsMenu " ); 
        final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
        if (!application.ShowStatusBarOption.getValue() &&
            application.ShowStatusBarWhenMenuIsActiveOption.getValue()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }	    
        super.invalidateOptionsMenu();
    }

    @Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		if (!application.ShowStatusBarOption.getValue() &&
			application.ShowStatusBarWhenMenuIsActiveOption.getValue()) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
	Log.v(TAG, "SEAN_LOG  onNewIntent " ); 
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			final String pattern = intent.getStringExtra(SearchManager.QUERY);
			final Handler successHandler = new Handler() {
				public void handleMessage(Message message) {
					ourTextSearchPanel.show(true);
				}
			};
			final Handler failureHandler = new Handler() {
				public void handleMessage(Message message) {
					UIUtil.showErrorMessage(FBReader.this, "textNotFound");
					ourTextSearchPanel.StartPosition = null;
				}
			};
			final Runnable runnable = new Runnable() {
				public void run() {
					ourTextSearchPanel.initPosition();
					final FBReaderApp fbReader = (FBReaderApp)FBReaderApp.Instance();
					fbReader.TextSearchPatternOption.setValue(pattern);
					if (fbReader.getTextView().search(pattern, true, false, false, false) != 0) {
						successHandler.sendEmptyMessage(0);
					} else {
						failureHandler.sendEmptyMessage(0);
					}
				}
			};
			UIUtil.wait("search", runnable, this);
			startActivity(new Intent(this, getClass()));
		} else {
			super.onNewIntent(intent);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
              Log.v(TAG, "SEAN_LOG  onStart " ); 
		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();

		final int fullScreenFlag =
			application.ShowStatusBarOption.getValue() ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
		if (fullScreenFlag != myFullScreenFlag) {
			finish();
			startActivity(new Intent(this, this.getClass()));
		}

		final RelativeLayout root = (RelativeLayout)findViewById(R.id.root_view);
		if (!ourTextSearchPanel.hasControlPanel()) {
			ourTextSearchPanel.createControlPanel(this, root);
		}
		if (!ourNavigatePanel.hasControlPanel()) {
			ourNavigatePanel.createControlPanel(this, root);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			sendBroadcast(new Intent(getApplicationContext(), KillerCallback.class));
		} catch (Throwable t) {
		}
		ControlButtonPanel.restoreVisibilities(FBReaderApp.Instance());
	}

	@Override
	public void onPause() {
		ControlButtonPanel.saveVisibilities(FBReaderApp.Instance());
		super.onPause();
	}

	@Override
	public void onStop() {
		ControlButtonPanel.removeControlPanels(FBReaderApp.Instance());
		super.onStop();
	}

	@Override
	protected FBReaderApp createApplication(ZLFile file) {
	Log.v(TAG, "SEAN_LOG  createApplication " ); 
		if (SQLiteBooksDatabase.Instance() == null) {
			new SQLiteBooksDatabase(this, "READER");
		}
		return new FBReaderApp(file != null ? file.getPath() : null);
	}

	@Override
	public boolean onSearchRequested() {
		final FBReaderApp fbreader = (FBReaderApp)FBReaderApp.Instance();
		ControlButtonPanel.saveVisibilities(fbreader);
		ControlButtonPanel.hideAllPendingNotify(fbreader);
		final SearchManager manager = (SearchManager)getSystemService(SEARCH_SERVICE);
		manager.setOnCancelListener(new SearchManager.OnCancelListener() {
			public void onCancel() {
				ControlButtonPanel.restoreVisibilities(fbreader);
				manager.setOnCancelListener(null);
			}
		});
		startSearch(fbreader.TextSearchPatternOption.getValue(), true, null, false);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final FBReaderApp fbreader = (FBReaderApp)FBReaderApp.Instance();
		switch (requestCode) {
			case REPAINT_CODE:
			{
				final BookModel model = fbreader.Model;
				if (model != null) {
					final Book book = model.Book;
					if (book != null) {
						book.reloadInfoFromDatabase();
						ZLTextHyphenator.Instance().load(book.getLanguage());
					}
				}
				fbreader.clearTextCaches();
				fbreader.getViewWidget().repaint();
				break;
			}
			case CANCEL_CODE:
				fbreader.runCancelAction(resultCode);
				break;
		}
	}

	public void navigate() {
		ourNavigatePanel.runNavigation();
	}

	private void addMenuItem(Menu menu, String actionId, int iconId) {
		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		application.myMainWindow.addMenuItem(menu, actionId, iconId);
	}

	private void addMenuItem(Menu menu, String actionId) {
		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		application.myMainWindow.addMenuItem(menu, actionId, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		addMenuItem(menu, ActionCode.SHOW_LIBRARY, R.drawable.ic_menu_library);
		addMenuItem(menu, ActionCode.SHOW_NETWORK_LIBRARY, R.drawable.ic_menu_networklibrary);
		addMenuItem(menu, ActionCode.SHOW_TOC, R.drawable.ic_menu_toc);
		addMenuItem(menu, ActionCode.SHOW_BOOKMARKS, R.drawable.ic_menu_bookmarks);
		addMenuItem(menu, ActionCode.SWITCH_TO_NIGHT_PROFILE, R.drawable.ic_menu_night);
		addMenuItem(menu, ActionCode.SWITCH_TO_DAY_PROFILE, R.drawable.ic_menu_day);
		addMenuItem(menu, ActionCode.SEARCH, R.drawable.ic_menu_search);
//sean_0517		addMenuItem(menu, ActionCode.SHOW_PREFERENCES);
               addMenuItem(menu, ActionCode.SHOW_PREFERENCES,R.drawable.ic_popup_settings);
		addMenuItem(menu, ActionCode.SHOW_BOOK_INFO);
              addMenuItem(menu, ActionCode.ROTATE,R.drawable.ic_popup_orientation);
//sean_0517              
		addMenuItem(menu, ActionCode.INCREASE_FONT,R.drawable.quickaction_arrow_up);
		addMenuItem(menu, ActionCode.DECREASE_FONT,R.drawable.quickaction_arrow_down);
		addMenuItem(menu, ActionCode.SHOW_NAVIGATION,R.drawable.ic_tab_selected_recent);

		final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
		application.myMainWindow.refreshMenu();

		return true;
	}


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_options_actions, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
  
        switch (item.getItemId()) {
            case R.id.show_library:
                Log.v(TAG, "SEAN_LOG  show_library " ); 
                ShowLibraryAction showlibaction=new ShowLibraryAction(this,fbReader);
                break;
            case R.id.network_library:
             Log.v(TAG, "SEAN_LOG  network_library " ); //new ShowNetworkLibraryAction(this, fbReader)
              ShowNetworkLibraryAction shownetlibaction=new ShowNetworkLibraryAction(this,fbReader);
                break;
            case R.id.show_bookmark:
 
                break;
            case R.id.show_night:
  
                break;
            case R.id.show_day:
    
                break;
            case R.id.show_search:
                
                break;
            case R.id.show_preference:
                
                break;
            case R.id.show_zoomin:
                
                break;
            case R.id.show_zoomout:
                
                break;
            case R.id.show_navi:
                
                break;
            case R.id.show_toc:
                
                break;
            case R.id.show_bookinfo:
                
                break;
            case R.id.increase_font:
                
                break;  
            case R.id.decrease_font:
                
                break;                    
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
*/    
//sean_0517
    public void onClick(View v) {
        final ActionBar bar = getActionBar();
        int flags = 0;
        switch (v.getId()) {
            case R.id.toggle_home_as_up:
                flags = ActionBar.DISPLAY_HOME_AS_UP;
                break;
            case R.id.toggle_show_home:
                flags = ActionBar.DISPLAY_SHOW_HOME;
                break;
            case R.id.toggle_use_logo:
                flags = ActionBar.DISPLAY_USE_LOGO;
                break;
            case R.id.toggle_show_title:
                flags = ActionBar.DISPLAY_SHOW_TITLE;
                break;
            case R.id.toggle_show_custom:
                flags = ActionBar.DISPLAY_SHOW_CUSTOM;
                break;

            case R.id.toggle_navigation:
                bar.setNavigationMode(
                        bar.getNavigationMode() == ActionBar.NAVIGATION_MODE_STANDARD
                                ? ActionBar.NAVIGATION_MODE_TABS
                                : ActionBar.NAVIGATION_MODE_STANDARD);
                return;
            case R.id.cycle_custom_gravity:
                ActionBar.LayoutParams lp = (ActionBar.LayoutParams) mCustomView.getLayoutParams();
                int newGravity = 0;
                switch (lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.LEFT:
                        newGravity = Gravity.CENTER_HORIZONTAL;
                        break;
                    case Gravity.CENTER_HORIZONTAL:
                        newGravity = Gravity.RIGHT;
                        break;
                    case Gravity.RIGHT:
                        newGravity = Gravity.LEFT;
                        break;
                }
                lp.gravity = lp.gravity & ~Gravity.HORIZONTAL_GRAVITY_MASK | newGravity;
                bar.setCustomView(mCustomView, lp);
                return;
        }

        int change = bar.getDisplayOptions() ^ flags;
        bar.setDisplayOptions(change, flags);
    }
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
    private void initializeButton() {
/*        
        mlocal = (Button)findViewById(R.id.local_library);
        mlocal.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
              onClickLocalLibraryButton();
            }
        });
        
        mnetwork = (Button)findViewById(R.id.network_library);
        mnetwork.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               onClickNetworkLibraryButton();
            }
        });

        mbookmark = (Button)findViewById(R.id.bookmark);
        mbookmark.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
             onClickBookmarkButton();
            }
        });

        msetting = (Button)findViewById(R.id.settings);
        msetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            onClickSettingButton();
            }
        });
 */       
    }
    private void onClickLocalLibraryButton() {

        
    }     
    private void onClickNetworkLibraryButton() {

        
    }           
    private void onClickBookmarkButton() {

        
    }           
     private void onClickSettingButton() {

        
    }              
    private void setAllButtonInVisible() {
/*        
        Button Button_A=(Button)findViewById(R.id.toggle_home_as_up);
        Button Button_B=(Button)findViewById(R.id.toggle_show_home);
        Button Button_C=(Button)findViewById(R.id.toggle_use_logo);
        Button Button_D=(Button)findViewById(R.id.toggle_show_title);
        Button Button_E=(Button)findViewById(R.id.toggle_show_custom);
        Button Button_F=(Button)findViewById(R.id.toggle_navigation);
        Button Button_G=(Button)findViewById(R.id.cycle_custom_gravity);
        
               
        ((Button) Button_A).setVisibility(View.INVISIBLE);
        ((Button) Button_B).setVisibility(View.INVISIBLE);  
        ((Button) Button_C).setVisibility(View.INVISIBLE);
        ((Button) Button_D).setVisibility(View.INVISIBLE);
        ((Button) Button_E).setVisibility(View.INVISIBLE);        
        ((Button) Button_F).setVisibility(View.INVISIBLE);    
        ((Button) Button_G).setVisibility(View.INVISIBLE);      
*/ 
/*        
        mlocal=(Button)findViewById(R.id.local_library);
        mnetwork=(Button)findViewById(R.id.network_library);
        mbookmark=(Button)findViewById(R.id.bookmark);
        msetting=(Button)findViewById(R.id.settings);
        
        ((Button) mlocal).setVisibility(View.INVISIBLE);
        ((Button) mnetwork).setVisibility(View.INVISIBLE);  
        ((Button) mbookmark).setVisibility(View.INVISIBLE);
        ((Button) msetting).setVisibility(View.INVISIBLE);
*/
    }    
    private void setAllButtonVisible() {
/*        
        Button Button_A=(Button)findViewById(R.id.toggle_home_as_up);
        Button Button_B=(Button)findViewById(R.id.toggle_show_home);
        Button Button_C=(Button)findViewById(R.id.toggle_use_logo);
        Button Button_D=(Button)findViewById(R.id.toggle_show_title);
        Button Button_E=(Button)findViewById(R.id.toggle_show_custom);
        Button Button_F=(Button)findViewById(R.id.toggle_navigation);
        Button Button_G=(Button)findViewById(R.id.cycle_custom_gravity);
        
               
        ((Button) Button_A).setVisibility(View.VISIBLE);
        ((Button) Button_B).setVisibility(View.VISIBLE);  
        ((Button) Button_C).setVisibility(View.VISIBLE);
        ((Button) Button_D).setVisibility(View.VISIBLE);
        ((Button) Button_E).setVisibility(View.VISIBLE);        
        ((Button) Button_F).setVisibility(View.VISIBLE);    
        ((Button) Button_G).setVisibility(View.VISIBLE);         
*/ 
 /*
        mlocal=(Button)findViewById(R.id.local_library);
        mnetwork=(Button)findViewById(R.id.network_library);
        mbookmark=(Button)findViewById(R.id.bookmark);
        msetting=(Button)findViewById(R.id.settings);
        ((Button) mlocal).setVisibility(View.VISIBLE);
        ((Button) mnetwork).setVisibility(View.VISIBLE);  
        ((Button) mbookmark).setVisibility(View.VISIBLE);
        ((Button) msetting).setVisibility(View.VISIBLE);
*/        
    }        

    private void setHomeVisible() {
        final ActionBar barHome = getActionBar();
        int flags = 0;
       
 /*       
        switch (v.getId()) {
            case R.id.toggle_home_as_up:
                flags = ActionBar.DISPLAY_HOME_AS_UP;
                break;
            case R.id.toggle_show_home:
                flags = ActionBar.DISPLAY_SHOW_HOME;
                break;
            case R.id.toggle_use_logo:
                flags = ActionBar.DISPLAY_USE_LOGO;
                break;
            case R.id.toggle_show_title:
                flags = ActionBar.DISPLAY_SHOW_TITLE;
                break;
            case R.id.toggle_show_custom:
                flags = ActionBar.DISPLAY_SHOW_CUSTOM;
                break;

            case R.id.toggle_navigation:
                bar.setNavigationMode(
                        bar.getNavigationMode() == ActionBar.NAVIGATION_MODE_STANDARD
                                ? ActionBar.NAVIGATION_MODE_TABS
                                : ActionBar.NAVIGATION_MODE_STANDARD);
                return;
            case R.id.cycle_custom_gravity:
                ActionBar.LayoutParams lp = (ActionBar.LayoutParams) mCustomView.getLayoutParams();
                int newGravity = 0;
                switch (lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.LEFT:
                        newGravity = Gravity.CENTER_HORIZONTAL;
                        break;
                    case Gravity.CENTER_HORIZONTAL:
                        newGravity = Gravity.RIGHT;
                        break;
                    case Gravity.RIGHT:
                        newGravity = Gravity.LEFT;
                        break;
                }
                lp.gravity = lp.gravity & ~Gravity.HORIZONTAL_GRAVITY_MASK | newGravity;
                barHome.setCustomView(mCustomView, lp);
                return;
        }
*/
        flags = ActionBar.DISPLAY_SHOW_TITLE;  
        int change = barHome.getDisplayOptions() ^ flags;
        barHome.setDisplayOptions(change, flags);
        
    }   
    
    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;

        private FileInputStream fis;
           
        private Integer[] mImageIds = {
                R.drawable.kasabian_kasabian,
                R.drawable.starssailor_silence_is_easy,
                R.drawable.killers_day_and_age,
                R.drawable.garbage_bleed_like_me,
                R.drawable.death_cub_for_cutie_the_photo_album,
                R.drawable.kasabian_kasabian,
                R.drawable.massive_attack_collected,
                R.drawable.muse_the_resistance,
                R.drawable.starssailor_silence_is_easy
        };

        private ImageView[] mImages;
        
        public ImageAdapter(Context c) {
            mContext = c;
            mImages = new ImageView[mImageIds.length];
        }
        public boolean createReflectedImages() {
                //The gap we want between the reflection and the original image
                final int reflectionGap = 4;
                
                
                int index = 0;
                for (int imageId : mImageIds) {
                    Bitmap originalImage = BitmapFactory.decodeResource(getResources(), 
                            imageId);
                    int width = originalImage.getWidth();
                    int height = originalImage.getHeight();
                    
           
                    //This will not scale but will flip on the Y axis
                    Matrix matrix = new Matrix();
                    matrix.preScale(1, -1);
                    
                    //Create a Bitmap with the flip matrix applied to it.
                    //We only want the bottom half of the image
                    Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);
                    
                        
                    //Create a new bitmap with same width but taller to fit reflection
                    Bitmap bitmapWithReflection = Bitmap.createBitmap(width 
                      , (height + height/2), Config.ARGB_8888);
                  
                   //Create a new Canvas with the bitmap that's big enough for
                   //the image plus gap plus reflection
                   Canvas canvas = new Canvas(bitmapWithReflection);
                   //Draw in the original image
                   canvas.drawBitmap(originalImage, 0, 0, null);
                   //Draw in the gap
                   Paint deafaultPaint = new Paint();
                   canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
                   //Draw in the reflection
                   canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);
                   
                   //Create a shader that is a linear gradient that covers the reflection
                   Paint paint = new Paint(); 
                   LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, 
                     bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, 
                     TileMode.CLAMP); 
                   //Set the paint to use this shader (linear gradient)
                   paint.setShader(shader); 
                   //Set the Transfer mode to be porter duff and destination in
                   paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN)); 
                   //Draw a rectangle using the paint with our linear gradient
                   canvas.drawRect(0, height, width, 
                     bitmapWithReflection.getHeight() + reflectionGap, paint); 
                   
                   ImageView imageView = new ImageView(mContext);
                   imageView.setImageBitmap(bitmapWithReflection);
                   imageView.setLayoutParams(new CoverFlow.LayoutParams(120, 180));
                   imageView.setScaleType(ScaleType.MATRIX);
                   mImages[index++] = imageView;
                   
                }
                return true;
        }

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            //Use this code if you want to load from resources
            //ImageView i = new ImageView(mContext);
            //i.setImageResource(mImageIds[position]);
            //i.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
            //i.setScaleType(ImageView.ScaleType.MATRIX);           
            //return i;
            
            return mImages[position];
        }
         /** Returns the size (0.0f to 1.0f) of the views 
         * depending on the 'offset' to the center. */ 
         public float getScale(boolean focused, int offset) { 
           /* Formula: 1 / (2 ^ offset) */ 
             return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset))); 
         } 

    }

//sean_0517
    
}
